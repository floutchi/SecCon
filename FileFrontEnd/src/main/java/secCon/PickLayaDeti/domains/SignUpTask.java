package secCon.PickLayaDeti.domains;

public class SignUpTask implements TaskProcessor {

    @Override
    public boolean check(String message){
        //verify regex signup
        return false;
    }

    @Override
    public void execute(String message){
        //decompose le regex et executer le code en fonction
    }
}
