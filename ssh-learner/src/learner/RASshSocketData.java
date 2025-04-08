package learner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RASshSocketData {
    private String msg;

    public RASshSocketData() {
        this.msg = "";
    }

    public RASshSocketData(RASshOutput input) {
        this.msg = String.format("{ \"msg\": \"%s\",", input.getName());
        for (RASshParams param : input.getParams()) {
            this.msg += String.format("\"%s\": \"%s\",", param.getName(), param.getValue());
        }
        this.msg += "}";
        // sending json example:
        // { "msg":"KEXINIT",
        // "kex_algorithms":"ext-info-c",
        // }
        System.out.println("JSON: " + this.msg);
    }

    public String getMsg() {
        return msg;
    }

    public RASshInput getSocketOutput(String jsonObj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonObj); // Parse JSON

            // Extract 'msg' as the input name
            String name = rootNode.get("msg").asText();

            // Extract all other key-value pairs as RASshParams
            List<RASshParams> paramsList = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String paramName = field.getKey();
                String paramValue = field.getValue().asText();

                // Skip 'msg' as it's already used as name
                if (!paramName.equals("msg")) {
                    paramsList.add(new RASshParams(paramName, "STRING", paramValue, "java.lang.String"));
                }
            }

            return new RASshInput(name, paramsList);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}