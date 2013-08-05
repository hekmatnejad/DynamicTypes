package kb.tester;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 6/7/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class Problem {
    String problemName = "none";
    public enum Severity  {HIGH, MODERATE, LOW};
    Severity severity = Severity.LOW;

    public Problem(String problemName) {
        this.problemName = problemName;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getProblemName() {
        return problemName;
    }
}
