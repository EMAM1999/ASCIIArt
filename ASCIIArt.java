
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates
 and open the template in the editor.
 */
/**

 @author EMAM
 */
public class ASCIIArt {


          private static final int defaultThreadsNumber = 5;
          private static int threadsNumber = defaultThreadsNumber;



          public static float getSpeed() {
                    return (float) threadsNumber / defaultThreadsNumber;
          }



          /**
           The speed of process is reduce due to the number of active threads <br>
           The default number of threads is 5 which has speed 1<br>
           The increasing of the speed of value lead to increasing of threads number

           @param speed the ratio between the actual number of threads and the default number of thread -5-
           */
          public static void setSpeed(float speed) {
                    threadsNumber = (int) (defaultThreadsNumber * speed);
          }

          private static final char PIXEL_CHAR[] = { 'M' , '$' , 'Q' , 'O' , 'o' , '|' , '*' , '^' , ':' , '=' , 's' , 'x' , '\\' , '.' , ' ' };

          private static final String COLORS[] = {
                    ConsoleColors.BLACK_DARK ,
                    ConsoleColors.BLUE_DARK ,
                    ConsoleColors.BLUE ,
                    ConsoleColors.RED_DARK ,
                    ConsoleColors.RED ,
                    ConsoleColors.PURPLE_DARK ,
                    ConsoleColors.GREEN_DARK ,
                    ConsoleColors.YELLOW_DARK ,
                    ConsoleColors.PURPLE ,
                    ConsoleColors.BLUE_BRIGHT ,
                    ConsoleColors.CYAN ,
                    //ConsoleColors.GREEN,
                    ConsoleColors.CYAN_BRIGHT ,
                    ConsoleColors.GREEN_BRIGHT ,
                    ConsoleColors.YELLOW_BRIGHT ,
                    ConsoleColors.WHITE };



          public static String image2ASCII(String pathString) throws IOException {
                    return ASCIIArt.image2ASCII(pathString , 1 , false);
          }



          /**
           ratio is between 0 -> 1 -> ... <br>
           1 means read all pixels <br>
           0.5 means read half pixels read 1 ignore 1<br>
           0.25 means read read 1 ignore 3<br>

           @param pathString
           @param ratio

           @return

           @throws IOException
           */
          public static String image2ASCII(String pathString , double ratio , boolean isColored) throws IOException {
                    return ASCIIArt.image2ASCII(new File(pathString) , ratio , isColored);
          }



          public static String image2ASCII(File file , double ratio , boolean isColored) throws IOException {
                    BufferedImage img = ImageIO.read(file);
                    return image2ASCII(img , ratio , isColored);
          }



          public static String image2ASCII(BufferedImage img , double ratio , boolean isColored) throws IOException {
                    int height = img.getHeight();
                    int width = img.getWidth();
                    int RGBRangeOfPixcelChar = 255 / PIXEL_CHAR.length; // the range of colors which  has the same character

                    ThreadGroup TG = new ThreadGroup("imageWorker");

                    A2I[] array = new A2I[threadsNumber];
                    for ( int i = 0 ; i < threadsNumber ; i++ ) {
                              array[i] = new A2I(img , height * i / threadsNumber , height * (i + 1) / threadsNumber , width , ratio , RGBRangeOfPixcelChar , isColored);
                              new Thread(TG , array[i]).start();

                    }

                    while ( TG.activeCount() != 0 ) {
//                              hold till all threads done
                    }

                    String result = ""; // the final string which will be displayed
                    for ( A2I ele : array ) {
                              result += ele;
                    }
                    return result + ConsoleColors.RESET;
          }

          static class A2I implements Runnable {


                    private String str = ""; // the ASCII string
                    private final int start; // the start point of the height of the image
                    private final int end; // the end point of the height of the image
                    private final double ratio;
                    private final double width; // the width of the image

                    private final BufferedImage img;
                    private final int RGBRangeOfPixcelChar;
                    private final boolean isColored;



                    public A2I(BufferedImage img , int start , int end , double width , double ratio , int RGBRangeOfPixcelChar , boolean isColored) {
                              this.start = start;
                              this.end = end;
                              this.ratio = ratio;
                              this.width = width;
                              this.img = img;
                              this.RGBRangeOfPixcelChar = RGBRangeOfPixcelChar;
                              this.isColored = isColored; // add the color code or not
                    }



                    @Override
                    public void run() {
                              for ( double i = start ; i < end ; i += (1 / ratio) ) {
                                        char lastChar = 0;
                                        for ( double j = 0 ; j < width ; j += (1 / ratio) ) {
                                                  if ( (int) j == j ) { // if the ratio is greater than 1 ; some pixels will repeated 
                                                            int color = img.getRGB((int) j , (int) i);
                                                            int r = color >> 16 & 0xff;
                                                            int g = color >> 8 & 0xff;
                                                            int b = color & 0xff;

                                                            int average = (r + g + b) / 3;
                                                            int index = (average - 1) / RGBRangeOfPixcelChar; // in which range do the value exist

                                                            lastChar = PIXEL_CHAR[index]; // get the character of this range
                                                            this.str += ((isColored ? COLORS[index] : "") + " " + lastChar);

                                                  } else {
                                                            this.str += (" " + lastChar);
                                                  }
                                        }
                                        this.str += ("\n"); // go to the next line
                              }
                    }



                    @Override
                    public String toString() {
                              return this.str;
                    }

          }

}

class ConsoleColors {


          public static final String LIGHT = "\u001B[1m";
          public static final String HEAVY = "\u001B[2m";
          public static final String PREVIES = "\u001B[40m";
          // Reset
          public static final String RESET = "\033[0m"; // Text Reset
          // Bright
          public static final String BRIGHT = "\033[1m"; // Text Bright
          // Dark
          public static final String DARK = "\033[2m"; // Text Dark

          // Regular Colors
          public static final String BLACK = "\033[0;30m"; // BLACK
          public static final String RED = "\033[0;31m"; // RED
          public static final String GREEN = "\033[0;32m"; // GREEN
          public static final String YELLOW = "\033[0;33m"; // YELLOW
          public static final String BLUE = "\033[0;34m"; // BLUE
          public static final String PURPLE = "\033[0;35m"; // PURPLE
          public static final String CYAN = "\033[0;36m"; // CYAN
          public static final String WHITE = "\033[0;37m"; // WHITE

          // High Intensity
          public static final String BLACK_BRIGHT = "\033[1;30m"; // BLACK
          public static final String RED_BRIGHT = "\033[1;31m"; // RED
          public static final String GREEN_BRIGHT = "\033[1;32m"; // GREEN
          public static final String YELLOW_BRIGHT = "\033[1;33m"; // YELLOW
          public static final String BLUE_BRIGHT = "\033[1;34m"; // BLUE
          public static final String PURPLE_BRIGHT = "\033[1;35m"; // PURPLE
          public static final String CYAN_BRIGHT = "\033[1;36m"; // CYAN
          public static final String WHITE_BRIGHT = "\033[1;37m"; // WHITE

          // Low Intensity
          public static final String BLACK_DARK = "\033[2;30m"; // BLACK
          public static final String RED_DARK = "\033[2;31m"; // RED
          public static final String GREEN_DARK = "\033[2;32m"; // GREEN
          public static final String YELLOW_DARK = "\033[2;33m"; // YELLOW
          public static final String BLUE_DARK = "\033[2;34m"; // BLUE
          public static final String PURPLE_DARK = "\033[2;35m"; // PURPLE
          public static final String CYAN_DARK = "\033[2;36m"; // CYAN
          public static final String WHITE_DARK = "\033[2;37m"; // WHITE

          // Background
          public static final String RESET_BACKGROUND = "\033[40m"; // BLACK
          public static final String RED_BACKGROUND = "\033[41m"; // RED
          public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN
          public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
          public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
          public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
          public static final String CYAN_BACKGROUND = "\033[46m"; // CYAN
          public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE

}

class Test {


          public static final String DESKTOP = "C:\\Users\\EMAM\\Desktop\\";



          public static void main(String[] args) throws InterruptedException , IOException {
                    ASCIIArt.setSpeed(10);
                    System.out.println(ASCIIArt.image2ASCII(DESKTOP + "pp.jpg"));
          }

}
