import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
  * Class to find SNPs in linkage disequilibrium.
  * @author Divya Manoharan
  */
public class LDPair {

  /** Store the SNPs pertaining to a single target gene */
  private ArrayList<String> SNPs = new ArrayList<String>();
  /** Declare an instane of PrintStream */
  private PrintStream o;

  /**
    * Constructor to initialize LDPair.
    */
  private LDPair() {}
  /**
    * Method to read the file containing all SNPs related to a target gene and store them in an ArrayList.
    * @param inputFileName the file to be read
    */
  public void parseFile(String inputFileName) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(inputFileName));
      String x;
      /** Reads every SNP in the inputted file. */
      while ((x = br.readLine()) != null) {
        SNPs.add(x);
      }
    }
    /** If this exception is thrown, make sure the input file is in the correct location and that the command in the terminal does not have any errors. */
    catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException");
    }
    catch (IOException e) {
      System.out.println("IOException");
    }
  }

  /**
    * Method to connect to LDLink, input SNPs, and check for LD.
    */
  public void connect() throws InterruptedException {
    /** Connect to Google Chrome. Chrome must be downloaded for this program to work. */
    WebDriver driver = new ChromeDriver();
    /** Connect to the LDPair section of LDLink. */
    driver.get("https://analysistools.nci.nih.gov/LDlink/?tab=ldpair");
    /** Pause long enough to select a population of choice. */
    Thread.sleep(6000);
    /** Compare every SNP in the list to every other SNP in the list to check for LD. */
    for (int i = 0; i < SNPs.size(); i++) {
      driver.findElement(By.id("ldpair-snp1")).sendKeys(SNPs.get(i));
      for (int j = i + 1; j < SNPs.size() - 1; j++) {
        driver.findElement(By.id("ldpair-snp2")).sendKeys(SNPs.get(j));
        driver.findElement(By.id("ldpair")).click();
        try {
          String r2 = new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr//td//span[@data-bind='text: statistics.r2']"))).getText();
          /** Check if the SNPs are in LD using an r2 value of 0.8. */
          if (Double.parseDouble(r2) >= 0.8) {
            System.out.println(SNPs.get(j));
            /** Remove one of the two SNPs which are in LD. */
            SNPs.remove(j);
            j--;
          }
        }
        catch (Exception e) {
          System.out.println(SNPs.get(j));
          /** Remove SNPs that either are not in the 1000G database or are on different chromosomes. */
          SNPs.remove(j);
          j--;
        }
        driver.findElement(By.id("ldpair-snp2")).clear();
      }
      driver.findElement(By.id("ldpair-snp1")).clear();
    }
  }

  /**
    * Method to create a file to print the relevant SNPs to.
    * @param gene the name of the target gene
    */
  public void createFile() {
    try {
      /** Create the file as well as a corresponding PrintStream. */
      o = new PrintStream(new File("list2"));
    }
    catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException");
    }
  }

  /**
    * Method to print all relevant SNPs to a .txt file.
    */
  public void writeToFile() {
    /** Set the output to the [gene]-SNPsOffical.txt file */
    System.setOut(o);
    for(String x : SNPs)
      System.out.println(x);
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
  }

  /**
    * Main method to run the program. Terminal command and other relevant documentation contained within the README file.
    * @param args the arguments necessary to run the program
    */
  public static void main(String[] args) throws InterruptedException {
    LDPair r = new LDPair();
    r.parseFile(args[0]);
    r.connect();
    r.createFile();
    r.writeToFile();
    System.exit(-1);
  }

}
