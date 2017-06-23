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
  * Class to input SNPs into Braineac and parse the generated files to determine which SNPs affect the inputted target genes.
  * @author Divya Manoharan
  */
public class ReadFromBraineac {

  /** Store the SNPs pertaining to a single target gene */
  private ArrayList<String> SNPs = new ArrayList<String>();
  /** Declare an instane of PrintStream */
  private PrintStream o;
  /** Store the RSID of the SNP being inputted into Braineac */
  private String rsid;
  /** Store the RSIDs of all SNPs which affect the targeted gene */
  private ArrayList<String> finalRSIDs = new ArrayList<String>();

  /**
    * Constructor to initialize a ReadFromBraineac object.
    */
  public ReadFromBraineac(){}

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
    * Method to access braineac.org, download, and read documentation corresponding to the inputted SNP.
    * @param gene the name of the target gene
    */
  public void connect(String gene) {
    /** Connect to Google Chrome. Chrome must be downloaded for this program to work. */
    WebDriver driver = new ChromeDriver();
    String originalHandle;
    /** Iterates through every SNP in the inputted file. */
    for (int i = 0; i < SNPs.size(); i++) {
      /** Accesses the HTML source for braineac.org */
      driver.get("http://peana-od.inf.um.es:8080/UKBECv12/");
      originalHandle = driver.getWindowHandle();
      for (String handle : driver.getWindowHandles()) {
        if (!handle.equals(originalHandle)) {
          driver.switchTo().window(handle);
          driver.close();
        }
      }
      driver.switchTo().window(originalHandle);
      driver.findElement(By.id("tabSNP")).click();
      /** Store the RSID of the current SNP being analyzed. */
      setRSID(SNPs.get(i));
      driver.findElement(By.id("snpList")).clear();
      driver.findElement(By.id("snpList")).sendKeys(this.rsid);
      driver.findElement(By.id("snpListSubmit")).click();
      try {
        new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOfElementLocated(By.id("downloadEQTL")));
        /** Downloads the cisEQTL document containing eQTL information corresponding to the inputted SNP. */
        driver.findElement(By.id("downloadEQTL")).click();
      }
      catch (Exception e) {
      }
      File f = new File("C:\\Users\\dmanohar\\Downloads", "cisEQTL.tsv");
      /** Checks whether a file was downloaded (some SNPs do not exist in the braineac database because they do not affect genes stored in the database). */
      if (f.exists()) {
        /** Read the cisEQTL file. */
        readEQTLFile(gene, f);
        /** Renames file after it is read. After each gene is parsed, it is imperative to clean out the downloads folder. */
        f.renameTo(new File("C:\\Users\\dmanohar\\Downloads", ((Double)(Math.random() * 2000)).toString()));
      }
    }
    driver.quit();
  }

  /**
    * Method to read the downloaded cisEQTL file.
    * @param gene the name of the target gene
    * @param f the cisEQTL file
    */
  public void readEQTLFile(String gene, File f) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(f));
      ArrayList<String> arr = new ArrayList<String>();
      String x;
      /** Reads the inputted file word by word into an ArrayList. */
      while ((x = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(x);
        while (st.hasMoreTokens()) {
          arr.add(st.nextToken());
        }
      }
      /** Iterates through all words in the inputted file to check whether the target gene is contained within the file. */
      for (int j = 0; j < arr.size(); j++) {
        if (arr.get(j).compareTo(gene) == 0) {
          /** If the target gene is contained within the file, the RSID of the SNP in question is added to an ArrayList storing all relevant SNPs. */
          finalRSIDs.add(this.rsid);
          break;
        }
      }
      br.close();
    }
    catch (FileNotFoundException e) {
      return;
    }
    catch (IOException e) {
      System.out.println("IOException");
    }
  }

  /**
    * Method to print all relevant SNPs to a .txt file.
    */
  public void writeToFile() {
    /** Set the output to the [gene]-SNPsOffical.txt file */
    System.setOut(o);
    for (String x : finalRSIDs)
      System.out.println(x);
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
  }

  /**
    * Method to create a file to print the relevant SNPs to.
    * @param gene the name of the target gene
    */
  public void createFile(String gene) {
    try {
      /** Create the file as well as a corresponding PrintStream. */
      o = new PrintStream(new File(gene + "-SNPsOffical.txt"));
    }
    catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException");
    }
  }

  /**
    * Method to set the RSID to that of the relevant SNP.
    * @param rsid the RSID of the relevant SNP
    */
  public void setRSID(String rsid) {
    this.rsid = rsid;
  }

  /**
    * Main method to run the program. Terminal command and other relevant documentation contained within the README file.
    * @param args the arguments necessary to run the program
    */
  public static void main(String[] args){
    ReadFromBraineac r = new ReadFromBraineac();
    r.createFile(args[0]);
    r.parseFile(args[1]);
    r.connect(args[0]);
    r.writeToFile();
    /** After analysis of any single target gene, the below statement will be printed. */
    System.out.println("Analysis of " + args[0] + " complete.");
    System.exit(-1);
  }

}
