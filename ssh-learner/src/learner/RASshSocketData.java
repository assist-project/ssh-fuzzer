package learner;

import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.words.OutputSymbol;
import de.learnlib.ralib.words.PSymbolInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

public class RASshSocketData {
    private String msg;

    public RASshSocketData() {
        this.msg = "";
    }

    public RASshSocketData(PSymbolInstance input) {
        Map<String, String> map = new HashMap<>();
        map.put("msg", input.getBaseSymbol().getName());
        for (DataValue<?> param : input.getParameterValues()) {
            map.put(param.getType().getName(), param.getId().toString());
        }
        // sending json example:
        // { "msg":"KEXINIT",
        // "kex_algorithms":"ext-info-c",
        // }
        this.msg = new Gson().toJson(map);
        System.out.println("JSON: " + this.msg);
    }

    public String getMsg() {
        return msg;
    }

    public PSymbolInstance getSocketOutput(String jsonObj) {
        List<DataType> types = new ArrayList<>();
        List<DataValue<?>> values = new ArrayList<>();

        OutputSymbol symbol = new OutputSymbol("O_"+jsonObj, types.toArray(new DataType[0]));
        System.out.println("output from mapper: " + symbol.toString());

        return new PSymbolInstance(symbol, values.toArray(new DataValue[0]));
    }
}