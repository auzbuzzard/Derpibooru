package derpibooru.derpy.server.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruFilter;

public class FilterListParser implements ServerResponseParser<List<DerpibooruFilter>> {
    @Override
    public List<DerpibooruFilter> parseResponse(String rawResponse) throws JSONException {
        JSONObject json = new JSONObject(rawResponse);

        /* TODO: parse complex filters */
        List<DerpibooruFilter> filters = new ArrayList<>();
        filters.addAll(parseFilterArray(json.getJSONArray("system_filters")));
        if (!json.isNull("user_filters")) {
            filters.addAll(parseFilterArray(json.getJSONArray("user_filters")));
        }
        /* TODO: look into "search_filters" — what is it? how to handle it? */
        /*JSONArray searchFilters = json.getJSONArray("search_filters");*/

        return filters;
    }

    private List<DerpibooruFilter> parseFilterArray(JSONArray array) throws JSONException {
        List<DerpibooruFilter> output = new ArrayList<>();

        int size = array.length();
        for (int x = 0; x < size; x++) {
            JSONObject f = array.getJSONObject(x);

            /* TODO: clean-up */
            List<Integer> hiddenIds = intListFromArray(f.getJSONArray("hidden_tag_ids"));
            List<Integer> spoileredIds = intListFromArray(f.getJSONArray("spoilered_tag_ids"));
            List<String> hidden = stringListFromArray(f.getJSONArray("hidden_tags"));
            List<String> spoilered = stringListFromArray(f.getJSONArray("spoilered_tags"));

            /* for some reason, Derpibooru sets "system" to "null" for non-system
             * filters, so getBoolean() does not work */
            boolean system = !f.isNull("system");

            output.add(new DerpibooruFilter(
                    f.getInt("id"), f.getString("name"), hiddenIds, spoileredIds,
                    f.getString("description"), hidden, spoilered, system, f.getInt("user_count")));
        }
        return output;
    }

    private List<Integer> intListFromArray(JSONArray array) throws JSONException {
        List<Integer> out = new ArrayList<>();
        for (int y = 0; y < array.length(); y++) {
            out.add(array.getInt(y));
        }
        return out;
    }

    private List<String> stringListFromArray(JSONArray array) throws JSONException {
        List<String> out = new ArrayList<>();
        for (int y = 0; y < array.length(); y++) {
            out.add(array.getString(y));
        }
        return out;
    }
}
