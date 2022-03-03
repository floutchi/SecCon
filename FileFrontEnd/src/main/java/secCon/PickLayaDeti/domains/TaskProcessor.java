package secCon.PickLayaDeti.domains;

public interface TaskProcessor {

    boolean check(String message); //check si le regex est valid

    void execute(String message); //execute task si regex valid

}
