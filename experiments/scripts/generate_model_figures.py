#!/usr/bin/env python3
"""
Generate SSH model figures for the thesis:
  {model}_full_colored.png  – full model, happy path in green  (appendix)
  {model}_happy_path.png    – happy-path-only subgraph          (body)
"""

import re, subprocess, os

GREEN_FILL = "#90EE90"
GRAY_FILL  = "#D3D3D3"
GREEN_EDGE = "darkgreen"
EDGE_W     = "2.5"

# ── Full-model layout overrides ────────────────────────────────────────────────
# The wide Mealy models render as a thin 7:1 ribbon by default, wasting the page
# width. These graph-level attributes reshape them to ~1.5:1 (the landscape page
# aspect) so they fill both dimensions. Node positions are scaled; node shapes
# are not distorted. Only models listed here are affected.
FULL_LAYOUT = {
    "mealy_dropbear": 'ratio="0.9"; size="14,10";',
    "mealy_openssh7": 'ratio="0.72"; size="14,10";',
}

# ── Happy path definitions ────────────────────────────────────────────────────
# Each entry: states on the happy path, dead/sink states, and the exact edges
# (src, tgt, label) that form the path.
HP = {
    # ── Mealy: Dropbear ───────────────────────────────────────────────────────
    "mealy_dropbear": {
        "states":      {"s0","s1","s3","s4","s6"},
        "dead_states": {"s2"},
        "edges": [
            ("s0","s1","KEXINIT / KEXINIT"),
            ("s1","s3","KEX30 / KEX31+NEWKEYS+BUFFERED"),
            ("s3","s4","NEWKEYS / NO_RESP"),
            ("s4","s4","SR_AUTH / SR_ACCEPT"),
            ("s4","s6","UA_PK_OK / UA_SUCCESS"),
            ("s6","s6","SR_CONN / SR_ACCEPT"),
        ],
    },
    # ── Mealy: OpenSSH 7 ──────────────────────────────────────────────────────
    "mealy_openssh7": {
        "states":      {"s0","s1","s2","s4","s5","s7"},
        "dead_states": {"s3"},
        "edges": [
            ("s0","s1","KEXINIT / KEXINIT"),
            ("s1","s2","KEX30 / KEX31+NEWKEYS+BUFFERED"),
            ("s2","s4","NEWKEYS / NO_RESP"),
            ("s4","s5","SR_AUTH / SR_ACCEPT"),
            ("s5","s5","SR_AUTH / SR_ACCEPT"),
            ("s5","s7","UA_PK_OK / UA_SUCCESS+GLOBAL_REQUEST"),
        ],
    },
    # ── RA: Dropbear RASTAR ───────────────────────────────────────────────────
    "ra_dropbear_star": {
        "states":      {"l0","l7","l8","l9","l11","l12","l13","l14","l16","l18","l24"},
        "dead_states": {"l5","l6"},
        "edges": [
            ("l0", "l7",  "?KEXINIT[]"),
            ("l7", "l8",  "!O_KEXINIT[]"),
            ("l8", "l9",  "?KEX30[]"),
            ("l9", "l11", "!O_KEX31+NEWKEYS+BUFFERED[]"),
            ("l11","l12", "?NEWKEYS[]"),
            ("l12","l13", "!O_NO_RESP[]"),
            ("l13","l16", "?SR_AUTH[]"),
            ("l16","l13", "!O_SR_ACCEPT[]"),
            ("l13","l14", "?UA_PK_OK[]"),
            ("l14","l18", "!O_UA_SUCCESS[]"),
            ("l18","l24", "?SR_CONN[]"),
            ("l24","l18", "!O_SR_ACCEPT[]"),
        ],
    },
    # ── RA: Dropbear RALAMBDA ─────────────────────────────────────────────────
    "ra_dropbear_lambda": {
        "states":      {"l0","l9","l3","l18","l1","l17","l5","l14","l15","l4","l13"},
        "dead_states": {"l7","l23"},
        "edges": [
            ("l0", "l9",  "?KEXINIT[]"),
            ("l9", "l3",  "!O_KEXINIT[]"),
            ("l3", "l18", "?KEX30[]"),
            ("l18","l1",  "!O_KEX31+NEWKEYS+BUFFERED[]"),
            ("l1", "l17", "?NEWKEYS[]"),
            ("l17","l5",  "!O_NO_RESP[]"),
            ("l5", "l14", "?SR_AUTH[]"),
            ("l14","l5",  "!O_SR_ACCEPT[]"),
            ("l5", "l15", "?UA_PK_OK[]"),
            ("l15","l4",  "!O_UA_SUCCESS[]"),
            ("l4", "l13", "?SR_CONN[]"),
            ("l13","l4",  "!O_SR_ACCEPT[]"),
        ],
    },
    # ── RA: OpenSSH 7 RASTAR ──────────────────────────────────────────────────
    "ra_openssh7_star": {
        "states":      {"l0","l7","l8","l9","l11","l12","l13","l15","l18","l19","l24"},
        "dead_states": {"l5","l6"},
        "edges": [
            ("l0", "l7",  "?KEXINIT[]"),
            ("l7", "l8",  "!O_KEXINIT[]"),
            ("l8", "l9",  "?KEX30[]"),
            ("l9", "l11", "!O_KEX31+NEWKEYS+BUFFERED[]"),
            ("l11","l12", "?NEWKEYS[]"),
            ("l12","l13", "!O_NO_RESP[]"),
            ("l13","l15", "?SR_AUTH[]"),
            ("l15","l18", "!O_SR_ACCEPT[]"),
            ("l18","l15", "?SR_AUTH[]"),
            ("l18","l19", "?UA_PK_OK[]"),
            ("l19","l24", "!O_UA_SUCCESS+GLOBAL_REQUEST[]"),
        ],
    },
    # ── RA: OpenSSH 7 RALAMBDA ────────────────────────────────────────────────
    "ra_openssh7_lambda": {
        "states":      {"l0","l10","l7","l19","l5","l18","l4","l15","l2","l17","l3"},
        "dead_states": {"l8","l27"},
        "edges": [
            ("l0", "l10", "?KEXINIT[]"),
            ("l10","l7",  "!O_KEXINIT[]"),
            ("l7", "l19", "?KEX30[]"),
            ("l19","l5",  "!O_KEX31+NEWKEYS+BUFFERED[]"),
            ("l5", "l18", "?NEWKEYS[]"),
            ("l18","l4",  "!O_NO_RESP[]"),
            ("l4", "l15", "?SR_AUTH[]"),
            ("l15","l2",  "!O_SR_ACCEPT[]"),
            ("l2", "l17", "?UA_PK_OK[]"),
            ("l17","l3",  "!O_UA_SUCCESS+GLOBAL_REQUEST[]"),
        ],
    },
}

BASE = "/Users/hasan101/Desktop/UU/thesis/my-ssh-fuzzer/experiments/orchestration"
OUT  = "/Users/hasan101/Desktop/UU/thesis/thesis_report_latex"

MODELS = {
    "mealy_dropbear":     f"{BASE}/learner_output_dropbear/learnedModel.dot",
    "mealy_openssh7":     f"{BASE}/learner_output_openssh7/learnedModel.dot",
    "ra_dropbear_star":   f"{BASE}/models_backups/learner_output_ra_dropbear_star_trans_auth/learnedModel.dot",
    "ra_dropbear_lambda": f"{BASE}/models_backups/learner_output_ra_dropbear_lambda_trans_auth/learnedModel.dot",
    "ra_openssh7_star":   f"{BASE}/models_backups/learner_output_openssh7_ra_star_trans_auth/learnedModel.dot",
    "ra_openssh7_lambda": f"{BASE}/models_backups/learner_output_openssh7_ra_lambda_trans_auth/learnedModel.dot",
}

# ── Helpers ───────────────────────────────────────────────────────────────────

def is_ra(content):
    return "digraph RA" in content

def node_re_ra(line):
    """Match RA node declaration: "l0" [...]"""
    return re.match(r'^"(l\d+)"\s*\[', line)

def node_re_mealy(line):
    """Match Mealy node declaration:  <ws>s0 [...]"""
    return re.match(r'^(\s+)(s\d+)\s*\[', line)

def edge_re_ra(line):
    """Match RA edge: "l0" -> "l7" [label=<?KEXINIT[]>]"""
    return re.match(r'^"(l\d+)"\s*->\s*"(l\d+)"\s*\[label=<([^>]+)>\]', line)

def edge_re_mealy(line):
    """Match Mealy edge: <ws>s0 -> s1 [label="KEXINIT / KEXINIT"];"""
    return re.match(r'^(\s+)(s\d+)\s*->\s*(s\d+)\s*\[label="([^"]+)"\]', line)

def inject_node_attrs(line, attrs):
    """Insert attrs at the start of the attribute list in a node line."""
    return re.sub(r'\[', f'[{attrs}, ', line, count=1)

def inject_edge_attrs(line, attrs):
    """Insert attrs at the start of the attribute list in an edge line."""
    return re.sub(r'\[label=', f'[{attrs}, label=', line, count=1)

# ── Full-model coloring ───────────────────────────────────────────────────────

def color_full_model(content, name):
    hp = HP[name]
    happy_set  = hp["states"]
    dead_set   = hp["dead_states"]
    edge_set   = {tuple(e) for e in hp["edges"]}
    ra         = is_ra(content)

    out = []
    for line in content.split('\n'):
        if ra:
            m = node_re_ra(line)
            if m:
                nid = m.group(1)
                if nid in happy_set:
                    line = inject_node_attrs(line, f'style=filled, fillcolor="{GREEN_FILL}"')
                elif nid in dead_set:
                    line = inject_node_attrs(line, f'style=filled, fillcolor="{GRAY_FILL}"')
                out.append(line); continue

            m = edge_re_ra(line)
            if m:
                src, tgt, lbl = m.group(1), m.group(2), m.group(3)
                if (src, tgt, lbl) in edge_set:
                    line = inject_edge_attrs(line, f'color={GREEN_EDGE}, penwidth={EDGE_W}')
                out.append(line); continue
        else:
            m = node_re_mealy(line)
            if m:
                nid = m.group(2)
                if nid in happy_set:
                    line = inject_node_attrs(line, f'style=filled, fillcolor="{GREEN_FILL}"')
                elif nid in dead_set:
                    line = inject_node_attrs(line, f'style=filled, fillcolor="{GRAY_FILL}"')
                out.append(line); continue

            m = edge_re_mealy(line)
            if m:
                src, tgt, lbl = m.group(2), m.group(3), m.group(4)
                if (src, tgt, lbl) in edge_set:
                    line = inject_edge_attrs(line, f'color={GREEN_EDGE}, penwidth={EDGE_W}')
                out.append(line); continue

        out.append(line)

    # Inject graph-level layout attrs right after the "digraph ... {" header
    if name in FULL_LAYOUT and out:
        out.insert(1, '\t' + FULL_LAYOUT[name])
    return '\n'.join(out)

# ── Happy-path-only subgraph ──────────────────────────────────────────────────

def make_happy_path_dot(content, name):
    hp = HP[name]
    happy_set = hp["states"]
    edge_set  = {tuple(e) for e in hp["edges"]}
    ra        = is_ra(content)

    header_line = content.split('\n')[0].strip()   # "digraph RA {" or "digraph g {"
    body = []

    # graph-level attrs for readability
    if ra:
        body += ['rankdir=LR;', 'nodesep=0.4;', 'ranksep=0.6;', 'node [fontsize=11];']
    else:
        body += ['\trankdir=LR;', '\tnodesep=0.4;', '\tranksep=0.6;', '\tnode [fontsize=11];']

    for line in content.split('\n')[1:]:
        stripped = line.strip()
        if stripped in ('{', '}', ''):
            continue

        if ra:
            # Keep the invisible start node and arrow
            if re.match(r'^""\s*[\[\-]', line):
                body.append(line)
                continue

            m = node_re_ra(line)
            if m and m.group(1) in happy_set:
                colored = inject_node_attrs(line, f'style=filled, fillcolor="{GREEN_FILL}"')
                body.append(colored)
                continue

            m = edge_re_ra(line)
            if m:
                src, tgt, lbl = m.group(1), m.group(2), m.group(3)
                if (src, tgt, lbl) in edge_set:
                    colored = inject_edge_attrs(line, f'color={GREEN_EDGE}, penwidth={EDGE_W}')
                    body.append(colored)
                continue
        else:
            # Keep __start0 lines
            if '__start0' in line:
                body.append(line)
                continue

            m = node_re_mealy(line)
            if m and m.group(2) in happy_set:
                colored = inject_node_attrs(line, f'style=filled, fillcolor="{GREEN_FILL}"')
                body.append(colored)
                continue

            m = edge_re_mealy(line)
            if m:
                src, tgt, lbl = m.group(2), m.group(3), m.group(4)
                if (src, tgt, lbl) in edge_set:
                    colored = inject_edge_attrs(line, f'color={GREEN_EDGE}, penwidth={EDGE_W}')
                    body.append(colored)
                continue

    lines = [header_line] + body + ['}']
    return '\n'.join(lines)

# ── PNG generation ────────────────────────────────────────────────────────────

def to_png(dot_src, out_path, dpi=150):
    res = subprocess.run(
        ['dot', f'-Gdpi={dpi}', '-Tpng', '-o', out_path],
        input=dot_src.encode(), capture_output=True
    )
    if res.returncode != 0:
        print(f"    graphviz error: {res.stderr.decode().strip()}")
        return False
    sz = os.path.getsize(out_path)
    print(f"    -> {out_path}  ({sz//1024} KB)")
    return True

# ── Main ──────────────────────────────────────────────────────────────────────

def main():
    os.makedirs(OUT, exist_ok=True)
    for name, dot_path in MODELS.items():
        print(f"\n=== {name} ===")
        with open(dot_path) as f:
            content = f.read()
        # graphviz 13+ rejects empty HTML labels label=<>; replace with label=""
        content = content.replace('label=<>', 'label=""')

        # 1. Full colored model (for appendix)
        colored_dot = color_full_model(content, name)
        full_png    = os.path.join(OUT, f"{name}_full_colored.png")
        to_png(colored_dot, full_png, dpi=120)

        # 2. Happy-path-only subgraph (for body)
        hp_dot  = make_happy_path_dot(content, name)
        hp_png  = os.path.join(OUT, f"{name}_happy_path.png")
        to_png(hp_dot, hp_png, dpi=150)

    print("\nAll done.")

if __name__ == "__main__":
    main()
