package kb.tester;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 6/7/13
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Patient {
    public String name = "";
    public Collection<Problem> problems = new HashSet<Problem>();

    public Patient(String name) {
        this.name = name;
    }

    public void addProblem(Problem problem){
        problems.add(problem);
    }

    public void removeProblem(Problem problem){
        problems.remove(problem);
    }

    public int numberOfProblems(){
        return problems.size();
    }

    public void printProblemsList(){
        int i = 0;
        for(Problem problem: problems){
            System.out.println("<" + i++ + "> "+ problem.problemName);
        }
    }
}
