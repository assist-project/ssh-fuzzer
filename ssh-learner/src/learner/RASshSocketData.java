package learner;

import de.learnlib.ralib.data.DataType;
import de.learnlib.ralib.data.DataValue;
import de.learnlib.ralib.words.PSymbolInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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
        try {
            Gson gson = new Gson();

            // Deserialize JSON into a Map
            Type mapType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = gson.fromJson(jsonObj, mapType);

            // Extract symbol name
            String symbolName = map.remove("msg");

            // Create list of DataTypes and DataValues from the rest
            List<DataType> types = new ArrayList<>();
            List<DataValue<?>> values = new ArrayList<>();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();

                // In a real case, you'd infer actual class type (here we just assume String)
                DataType type = new DataType(paramName, String.class);
                DataValue<?> value = new DataValue<>(type, paramValue);

                types.add(type);
                values.add(value);
            }

            // Create the ParameterizedSymbol
            RASshInput symbol = new RASshInput(symbolName, types.toArray(new DataType[0]));

            // Build and return the PSymbolInstance
            return new PSymbolInstance(symbol, values.toArray(new DataValue[0]));

        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}