package model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(FeasibleSolution.FeasibleSolutionAdapter.class)
public class FeasibleSolution {
    private final List<List<String>> groups;

    public FeasibleSolution(List<List<String>> groups) {
        this.groups = groups;
    }

    public List<List<String>> getGroups() {
        return groups;
    }

    public static FeasibleSolution aggregate(List<FeasibleSolution> feasibleSolutions) {
        List<List<String>> groups = new ArrayList<>();

        for (var solution : feasibleSolutions) {
            groups.addAll(solution.groups);
        }

        return new FeasibleSolution(groups);
    }

    public static class FeasibleSolutionAdapter extends TypeAdapter<FeasibleSolution> {


        @Override
        public void write(JsonWriter jsonWriter, FeasibleSolution feasibleSolution) throws IOException {
            jsonWriter.beginArray();
            for (List<String> group : feasibleSolution.groups) {
                jsonWriter.beginArray();
                for (String mutant : group) {
                    jsonWriter.value(mutant);
                }
                jsonWriter.endArray();
            }
            jsonWriter.endArray();
        }

        @Override
        public FeasibleSolution read(JsonReader jsonReader) throws IOException {
            List<List<String>> groups = new ArrayList<>();
            jsonReader.beginArray();

            while (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                List<String> group = new ArrayList<>();
                jsonReader.beginArray();
                while (jsonReader.peek() != JsonToken.END_ARRAY) {
                    group.add(jsonReader.nextString());
                }
                jsonReader.endArray();
                groups.add(group);
            }

            jsonReader.endArray();
            return new FeasibleSolution(groups);
        }
    }
}
