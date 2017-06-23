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
  * Class to input SNPs into GTEx and determine which SNPs have significant p-values (in the brain region).
  * @author Divya Manoharan
  */
public class ReadFromGTEx {

  /** Store the SNPs pertaining to a single target gene */
  private ArrayList<String> SNPs = new ArrayList<String>();
  /** Relevant tissues. */
  private String[] tissues = {"Brain_Anterior_cingulate_cortex_BA24", "Brain_Cortex", "Brain_Frontal_Cortex_BA9", "Brain_Hippocampus", "Brain_Hypothalamus"};
  /** Store GTEx data. */
  private ArrayList<GTExData> finalList = new ArrayList<GTExData>();
  /** Declare an instane of PrintStream */
  private PrintStream o;

  public ReadFromGTEx() {}

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
        SNPs.add(x.trim());
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
    * Method to connect to the GTEx Browser and extract significant SNPs.
    * @param gene the gene name
    */
  public void connect(String gene) {
    WebDriver driver = new ChromeDriver();
    driver.get("https://www.gtexportal.org/home/testyourown");
    for (String x : SNPs) {
      try {
        for (String y : tissues) {
          String temp = x + ", " + gene + ", " + y + "\n";
          new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOfElementLocated(By.id("testYourOwnBatchText"))).sendKeys(temp);
        }
        driver.findElement(By.id("testYourOwnBatchButton")).click();
        new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='testYourOwnBatchTable']//tbody//tr/td[4]")));
        List<WebElement> pval = driver.findElements(By.xpath("//table[@id='testYourOwnBatchTable']//tbody//tr/td[4]"));
        List<WebElement> tissues = driver.findElements(By.xpath("//table[@id='testYourOwnBatchTable']//tbody//tr/td[8]/a"));
        String rsid = driver.findElement(By.xpath("//table[@id='testYourOwnBatchTable']//tbody//tr/td[3]")).getText().split("\n")[0];
        for (int i = 0; i < pval.size(); i++) {
          if (Double.parseDouble(pval.get(i).getText()) < .05) {
            finalList.add(new GTExData(rsid, Double.parseDouble(pval.get(i).getText()), tissues.get(i).getText()));
          }
        }
      }
      catch (Exception e) {}
      driver.findElement(By.id("testYourOwnBatchText")).clear();
    }
  }

  /**
    * Method to generate a command that can be inputted into GTEx.
    * @param gene the gene name
    */
  public void generateOutput(String gene) {
    for (int i = 0; i < finalList.size(); i++) {
      for (int j = i + 1; j < finalList.size(); j++) {
        if (finalList.get(i).getRSID().equals(finalList.get(j).getRSID())) {
          if (finalList.get(i).getPvalue() < finalList.get(j).getPvalue())
            finalList.remove(i);
          else
            finalList.remove(j);
        }
      }
    }
    for (GTExData k : finalList) {
      switch(k.getExpr()) {
        case "Brain - Anterior cingulate cortex (BA24)" :
          k.setExpr("Brain_Anterior_cingulate_cortex_BA24");
          break;
        case "Brain - Cortex" :
          k.setExpr("Brain_Cortex");
          break;
        case "Brain - Frontal Cortex (BA9)" :
          k.setExpr("Brain_Frontal_Cortex_BA9");
          break;
        case "Brain - Hippocampus" :
          k.setExpr("Brain_Hippocampus");
          break;
        case "Brain - Hypothalamus" :
          k.setExpr("Brain_Hypothalamus");
          break;
      }
      System.out.println(k.getRSID().trim() + ", " + gene + ", " + k.getExpr());
    }
  }

  /**
    * Method to create a file to print the relevant data to.
    * @param outputFileName the name of the output file
    */
  public void createFile(String outputFileName) {
    try {
      /** Create the file as well as a corresponding PrintStream. */
      o = new PrintStream(new File(outputFileName + "-GTEx.txt"));
    }
    catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException");
    }
  }

  /**
    * Method to print all relevant data to a .txt file.
    * @param gene the relevant gene
    */
  public void writeToFile(String gene) {
    /** Set the output to the [gene]-SNPsOffical.txt file */
    System.setOut(o);
    generateOutput(gene);
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
  }

  public static void main(String[] args) {
    ReadFromGTEx g = new ReadFromGTEx();
    g.parseFile(args[0]);
    g.connect(args[1]);
    g.createFile(args[0]);
    g.writeToFile(args[1]);
    System.exit(0);
  }

}
