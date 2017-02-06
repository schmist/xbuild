import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by Stefan on 2/6/2017.
 */
public class BinaryAdapter implements JsonDeserializer<Binary> {

    @Override
    public Binary deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            Binary binary = new Gson().fromJson(jsonObject , Binary.class);
            binary.applyOutputPath();
            return binary;
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
