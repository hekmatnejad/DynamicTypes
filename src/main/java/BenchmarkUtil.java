import com.sun.japex.Japex;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/4/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkUtil {

    private static PipedInputStream pin = new PipedInputStream(10000);//10000
    private static PipedOutputStream out;


    public static boolean benchmarkThisMethod(Object obj, String methodName){

        Map<String,Double> lapTimes = new HashMap<String, Double>();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        PipedOutputStream pout;
        try {
            pout = new PipedOutputStream(pin);
            System.setOut(new PrintStream(pout,true));
            System.setErr(new PrintStream(pout,true));
        } catch (java.io.IOException io) {
        } catch (SecurityException se) {
        }


        java.lang.reflect.Method method = null;
        try {
            method = obj.getClass().getMethod(methodName);
            method.invoke(obj);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        System.setOut(originalOut);
        System.setErr(originalErr);

        byte[] bytes = null;
        try {
            bytes = new byte[pin.available()];
            pin.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(new String(bytes));
        return true;
    }
    //to use Jprofiler you have to use the below line as vm options
    //-Xms256m -Xmx1024m -XX:MaxPermSize=256m "-agentpath:I:\FixedPath\jprofiler_windows-x64_8_0_1\jprofiler8\bin\windows-x64\jprofilerti.dll=offline,id=109,config=I:\FixedPath\jprofiler_windows-x64_8_0_1\jprofiler8\bin\config.xml" "-Xbootclasspath/a:I:\FixedPath\jprofiler_windows-x64_8_0_1\bin\agent.jar"

    public static void main(String[] args) {
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

//        //OpencdsBenchmarking opencdsBenchmarking = new OpencdsBenchmarking();
//        //benchmarkThisMethod(opencdsBenchmarking,"testOpencdsBenchmark2TraitMethod");

//        openCDSBenchmark();
//        openCDSBenchmarkComplex();
        traitDonBenchmark();
//        highlyJoinBenchmark();
//        BasicMultiObjectBenchmark();
//        isAProfiling();
    }

    public static void openCDSBenchmark()
    {
        Japex japex = new Japex();
        japex.setHtml(true);
        japex.setOutputDirectory(new File("japex"));
//        japex.setOutputWriter( new PrintWriter(System.out));
        japex.run(  ClassLoader.getSystemResource("opencdsBnk-config.xml").getPath().replaceAll("%20", " ") );

    }

    public static void openCDSBenchmarkComplex()
    {
        Japex japex = new Japex();
        japex.setHtml(true);
        japex.setOutputDirectory(new File("japex"));
//        japex.setOutputWriter( new PrintWriter(System.out));
        japex.run(  ClassLoader.getSystemResource("opencdsBnk-configComplex.xml").getPath().replaceAll("%20", " ") );

    }

    public static void traitDonBenchmark()
    {
        Japex japex = new Japex();
        japex.setHtml(true);
        japex.setOutputDirectory(new File("japex"));
//        japex.setOutputWriter( new PrintWriter(System.out));
        japex.run(  ClassLoader.getSystemResource("traitDon-config.xml").getPath().replaceAll("%20", " ") );

    }

    public static void highlyJoinBenchmark()
    {
        Japex japex = new Japex();
        japex.setHtml(true);
        japex.setOutputDirectory(new File("japex"));
//        japex.setOutputWriter( new PrintWriter(System.out));
        japex.run(  ClassLoader.getSystemResource("highlyJoin-config.xml").getPath().replaceAll("%20", " ") );

    }

    public static void BasicMultiObjectBenchmark()
    {
        Japex japex = new Japex();
        japex.setHtml(true);
        japex.setOutputDirectory(new File("japex"));
//        japex.setOutputWriter( new PrintWriter(System.out));
        japex.run(  ClassLoader.getSystemResource("BasicMultiObject-config.xml").getPath().replaceAll("%20", " ") );

    }

    public static void isAProfiling()
    {
        Japex japex = new Japex();
        japex.setHtml(true);
        japex.setOutputDirectory(new File("japex"));
//        japex.setOutputWriter( new PrintWriter(System.out));
        japex.run(  ClassLoader.getSystemResource("isAProfiler-config.xml").getPath().replaceAll("%20", " ") );

    }

}
