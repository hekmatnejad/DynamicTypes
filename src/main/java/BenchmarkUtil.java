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

    private static PipedInputStream pin = new PipedInputStream(10000);
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

    public static void main(String[] args) {

//        //OpencdsBenchmarking opencdsBenchmarking = new OpencdsBenchmarking();
//        //benchmarkThisMethod(opencdsBenchmarking,"testOpencdsBenchmark2TraitMethod");

//        openCDSBenchmark();
//        openCDSBenchmarkComplex();
        traitDonBenchmark();
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

}
