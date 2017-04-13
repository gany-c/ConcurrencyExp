import java.time.Duration;
import java.time.Instant;

public class HelloWorld {

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		System.out.println("Hello Hi Chodiye, Jai mata di bolliye :)");
		long stopTime = System.nanoTime();
		System.out.println(stopTime - startTime);
	}

}
