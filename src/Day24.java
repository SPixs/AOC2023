import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Day24 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day24.txt"));

		// Part 1
		long startTime = System.nanoTime();
		List<Ray> allRays = new ArrayList<Ray>();
		for (String line : lines) {
			Vec3 origin = Vec3.split(line.split("@")[0]);
			Vec3 dir = Vec3.split(line.split("@")[1]);
			allRays.add(new Ray(origin, dir));
		}
		
		int result = 0;

		BigDecimal low = new BigDecimal("200000000000000");
		BigDecimal high = new BigDecimal("400000000000000");
		
		for (int i=0;i<allRays.size();i++) {
			for (int j=i+1;j<allRays.size();j++) {
				Ray ray1 = allRays.get(i);
				Ray ray2 = allRays.get(j);
 				BigDecimal intersection1 = ray1.computeIntersectionXY(ray2);
				BigDecimal intersection2 = ray2.computeIntersectionXY(ray1);
				if (intersection1 != null && intersection1.compareTo(BigDecimal.ZERO) >= 0 && intersection2.compareTo(BigDecimal.ZERO) >= 0) {
					Vec3 location = ray1.getLocation(intersection1);
					if (location.x.compareTo(low) < 0) continue;
					if (location.y.compareTo(low) < 0) continue;
					if (location.x.compareTo(high) > 0) continue;
					if (location.y.compareTo(high) > 0) continue;
					result++;
				}
			}
		}
		
		System.out.println("Result part 1 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		result = 0;
		
		System.out.println("Optimizing best fitting ray...");
		
		BigDecimal distance = null;
		
		Ray fittingRay = null;
		
		double temperature = 10000.0;
		BigDecimal t1 = BigDecimal.valueOf(0);
		BigDecimal t2 = BigDecimal.valueOf(10);
		
		while (true) {
			
			BigDecimal newT1 = t1.add(((Math.random() < 0.5) ? BigDecimal.valueOf(0.0001) : BigDecimal.TEN.pow(generateRandomExponent(1, 16))).multiply(BigDecimal.valueOf(Math.random() - 0.5)));
			BigDecimal newT2 = t2.add(((Math.random() < 0.5) ? BigDecimal.valueOf(0.0001) : BigDecimal.TEN.pow(generateRandomExponent(1, 16))).multiply(BigDecimal.valueOf(Math.random() - 0.5)));

			Vec3 p1 = allRays.get(0).getLocation(newT1);
			Vec3 p2 = allRays.get(1).getLocation(newT2);
			Ray newRay = new Ray(p1, p2.subtract(p1));
			BigDecimal newDistance = newRay.getDistance(allRays);
			
			double delta = distance == null ? -1 : newDistance.subtract(distance).doubleValue();
			
			if (distance == null || (delta != 0 && Math.random() < Math.exp(-delta/temperature))) {
				distance = newDistance;
				t1 = newT1;
				t2 = newT2;
				fittingRay = newRay;
				System.out.println(distance + " " + t1 + " " + t2 + " " + temperature);
			}
			temperature *= 0.999;
			if (distance.compareTo(BigDecimal.valueOf(1)) < 0) break;
		}
		
		final Ray tmpRay = fittingRay;
		final Ray firstRay = Collections.min(allRays, new Comparator<Ray>() {
			public int compare(Ray o1, Ray o2) { return o1.computeIntersectionXY(tmpRay).compareTo(o2.computeIntersectionXY(tmpRay)); }
		});
		BigDecimal elpased1 = firstRay.computeIntersectionXY(fittingRay);
		Vec3 intersection1 = firstRay.getLocation(elpased1);
		Ray otherRay = allRays.stream().filter(anyRay -> anyRay != firstRay).findAny().orElseThrow();
		BigDecimal elpased2 = otherRay.computeIntersectionXY(fittingRay);
		Vec3 intersection2 = otherRay.getLocation(elpased2);
		Vec3 direction = intersection2.subtract(intersection1).divide(elpased2.subtract(elpased1));
		
		Ray rockRay = new Ray(intersection1, direction);
		Vec3 rockOrigin = rockRay.getLocation(elpased1.negate());
		
		BigDecimal bigResult = rockOrigin.x.add(rockOrigin.y).add(rockOrigin.z).setScale(0, RoundingMode.HALF_UP);
		
		System.out.println("Result part 2 : " + bigResult + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	private static int generateRandomExponent(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
	
	public static class Ray {
		
		Vec3 origin = null;
		Vec3 direction = null;
		
		public Ray(Vec3 origin, Vec3 dir) {
			this.origin = origin;
			this.direction = dir;
		}

		public BigDecimal getDistance(List<Ray> allRays) {
			BigDecimal distance = BigDecimal.ZERO;
			for (Ray ray : allRays) {
				distance = distance.add(getDistance(ray));
			}
			return distance;
		}

		/**
		 * @param t
		 * @return the position along the ray
		 */
		public Vec3 getLocation(BigDecimal t) {
			BigDecimal x = origin.x.add(direction.x.multiply(t));
			BigDecimal y = origin.y.add(direction.y.multiply(t));
			BigDecimal z = origin.z.add(direction.z.multiply(t));
			return new Vec3(x, y, z);
		}
		
		public BigDecimal getDistanceToPoint(Vec3 point) {
		    Vec3 direction = this.direction;
		    Vec3 origin = this.origin;

		    // Calculate the vector from the ray's origin to the point
		    Vec3 delta = point.subtract(origin);

		    // Calculate the distance using the formula
		    BigDecimal distance = delta.cross(direction).norm().divide(direction.norm(), 10, RoundingMode.HALF_UP);
		    return distance;
		}
		
		/**
		 * @param otherRay
		 * @return the value along the direction, based on origin, when the intersection occurs in XY plan
		 */
		public BigDecimal computeIntersectionXY(Ray otherRay) {

			BigDecimal det = otherRay.direction.x.multiply(this.direction.y).subtract(otherRay.direction.y.multiply(this.direction.x));

            if (det.equals(BigDecimal.ZERO)) {
                // The rays are parallel, no intersection in XY plane
                return null;
            }

            // dx = bs.x - as.x
            // dy = bs.y - as.y
            //		det = bd.x * ad.y - bd.y * ad.x
            //		u = (dy * bd.x - dx * bd.y) / det
            
            BigDecimal dx = otherRay.origin.x.subtract(this.origin.x);
            BigDecimal dy = otherRay.origin.y.subtract(this.origin.y);

            BigDecimal t2 = dy.multiply(otherRay.direction.x)
            		.subtract(dx.multiply(otherRay.direction.y))
            		.divide(det, 10, RoundingMode.HALF_UP);
            
            return t2;
		}
		
        public BigDecimal getDistance(Ray other) {
        	Vec3 n = this.direction.cross(other.direction);
        	
        	// Handling the case where the rays are parallel
            if (n.isZero()) {
                return getDistanceToPoint(other.origin);
            }
        	
        	BigDecimal d = origin.subtract(other.origin).dot(n).divide(n.norm(), 10, RoundingMode.HALF_UP);
        	return d.abs();
        }
		
		@Override
		public String toString() {
			return origin+"@"+direction;
		}
	}
	
	public static class Vec3 {
		
		public BigDecimal x;
		public BigDecimal y;
		public BigDecimal z;

		public Vec3(double x, double y, double z) {
			this.x = BigDecimal.valueOf(x);
			this.y = BigDecimal.valueOf(y);
			this.z = BigDecimal.valueOf(z);
		}
		
		public Vec3 divide(BigDecimal divisor) {
			return new Vec3(x.divide(divisor, 10, RoundingMode.HALF_UP), y.divide(divisor, 10, RoundingMode.HALF_UP), z.divide(divisor, 10, RoundingMode.HALF_UP));
		}

		public Vec3() {
            this.x = BigDecimal.ZERO;
            this.y = BigDecimal.ZERO;
            this.z = BigDecimal.ZERO;
        }

		public boolean isZero() {
			return x.signum() == 0 && y.signum() == 0 && z.signum() == 0;
		}
		
        public BigDecimal norm() {
			return x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).sqrt(new MathContext(10));
		}

		public Vec3 subtract(Vec3 other) {
			return new Vec3(x.subtract(other.x), y.subtract(other.y), z.subtract(other.z));
		}

		public Vec3(BigDecimal x, BigDecimal y, BigDecimal z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public Vec3(Vec3 other) {
			this.x = other.x;
			this.y = other.y;
			this.z = other.z;
		}

		public static Vec3 split(String string) {
			Vec3 result = new Vec3();
			string = string.replace(" " , "");
			String[] split = string.split(",");
			result.x = new BigDecimal(split[0]);
			result.y = new BigDecimal(split[1]);
			result.z = new BigDecimal(split[2]);
			return result;
		}
        
        public BigDecimal dot(Vec3 other) {
        	return x.multiply(other.x).add(y.multiply(other.y)).add(z.multiply(other.z));
        }
        
        public Vec3 cross(Vec3 other) {
            BigDecimal newX = y.multiply(other.z).subtract(z.multiply(other.y));
            BigDecimal newY = z.multiply(other.x).subtract(x.multiply(other.z));
            BigDecimal newZ = x.multiply(other.y).subtract(y.multiply(other.x));
            return new Vec3(newX, newY, newZ);
        }
        
        @Override
        public String toString() {
        	return "["+x+","+y+","+z+"]";
        }
	}
}
