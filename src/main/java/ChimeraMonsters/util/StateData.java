package ChimeraMonsters.util;

import java.util.ArrayList;

public class StateData<T> {
    public ArrayList<T> runData = new ArrayList<>();
    public ArrayList<T> actData = new ArrayList<>();

    public void add(T data) {
        runData.add(data);
        actData.add(data);
    }

    public boolean contains(T data) {
        return runData.contains(data);
    }

    public boolean containsThisAct(T data) {
        return actData.contains(data);
    }

    public int size() {
        return runData.size();
    }

    public int sizeThisAct() {
        return actData.size();
    }

    public int countOf(T data) {
        return (int) runData.stream().filter(d -> d.equals(data)).count();
    }

    public int countOfThisAct(T data) {
        return (int) actData.stream().filter(d -> d.equals(data)).count();
    }

    public void clear() {
        runData.clear();
        clearThisAct();
    }

    public void clearThisAct() {
        actData.clear();
    }
}
