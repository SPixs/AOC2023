import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day20 {
	
	public static class Pulse {


		public Pulse(boolean isLow, Module out, Module in) {
			this.isLow = isLow;
			this.sourceModule = in;
			this.destinationModule = out;
		}
		public boolean isLow;
		public Module sourceModule;
		public Module destinationModule;
		
		public Collection<Pulse> process() {
			if (destinationModule.getName().contains("qt")) {
				Thread.yield();
			}
			return destinationModule.processPulse(isLow, sourceModule);
		}

		@Override
		public String toString() {
			return sourceModule + " " + (isLow ? "-low" : "-high") + " -> " + destinationModule;
		}
	}

	private final static boolean DEBUG = false;
	
	public static abstract class Module {
		
		protected List<Module> output = new ArrayList<Module>();
		public String[] outputNames;
		public int lowPulseCounter = 0;
		public List<Module> input = new ArrayList<Module>();
		
		public abstract void addInput(Module module);
		
		protected abstract Collection<Pulse> processPulse(boolean isLow, Module sourceModule);

		public Module(String[] outputs) {
			this.outputNames = outputs;
		}

		public void connect(Map<String, Module> modulesMap, Broadcaster broadcaster) {
			for (String outputName : outputNames) {
				if (outputName.equals("broadcaster")) {
					output.add(broadcaster);
				}
				else {
					Module module = modulesMap.get(outputName);
					if (module == null) {
						module = new TerminalModule(outputName);
						modulesMap.put(outputName, module);
					};
					output.add(module);
					module.addInput(this);
				}
			}
		}

		protected abstract String getName();
		
		@Override
		public String toString() {
			return getName();
		}

		protected abstract void reset();
	}
	
	public static class TerminalModule extends Module {

		private String name;

		public TerminalModule(String name) {
			super(new String[0]);
			this.name = name;
		}

		@Override
		protected Collection<Pulse> processPulse(boolean isLow, Module sourceModule) {
			if (isLow) lowPulseCounter++;
			return Collections.emptyList();
		}

		@Override
		public void addInput(Module module) {
			input.add(module);
		}
		
		@Override
		protected String getName() {
			return name;
		}

		@Override
		protected void reset() { lowPulseCounter = 0; }
	}
	
	public static class Button extends Module {
		
		public Button() {
			super(new String[] { "broadcaster" });
		}

		@Override
		public void addInput(Module module) {
		}
		
		@Override
		protected String getName() { return "button"; }

		public List<Pulse> generatePulses() {
			return output.stream().map(out -> new Pulse(true, out, this)).collect(Collectors.toList());
		}
		
		@Override
		protected Collection<Pulse> processPulse(boolean isLow, Module sourceModule) {
			throw new IllegalStateException();
		}
		
		@Override
		protected void reset() { lowPulseCounter = 0; }
	}
	
	public static class FlipFlop extends Module {
		
		private boolean isOff = true;
		private String name;
		
		public FlipFlop(String name, String[] outputs) {
			super(outputs);
			this.name = name;
		}

		@Override
		public void addInput(Module module) {
			input.add(module);
		}
		
		@Override
		protected String getName() {
			return name+"(flipflop)";
		}

		@Override
		protected Collection<Pulse> processPulse(boolean low, Module sourceModule) {
			if (low) lowPulseCounter++;
			if (low) { 
				isOff = !isOff; 
				return output.stream().map(out -> new Pulse(isOff, out, this)).collect(Collectors.toList());
			}
			return Collections.emptyList();
		}
		
		@Override
		protected void reset() {
			isOff = true;
			lowPulseCounter = 0;
		}

	}
	
	public static class Conjunction extends Module {
		
		private Map<Module, Boolean> inputIsLow = new LinkedHashMap<Module, Boolean>();
		private String name;
		
		public Conjunction(String name, String[] outputs) {
			super(outputs);
			this.name = name;
		}
		
		public void addInput(Module module) {
			inputIsLow.put(module, true);
			input.add(module);
		}
		
		@Override
		protected Collection<Pulse> processPulse(boolean low, Module sourceModule) {
			if (low) lowPulseCounter++;
			if (!inputIsLow.containsKey(sourceModule))
				throw new IllegalStateException();
			inputIsLow.put(sourceModule, low);
			boolean allHigh = inputIsLow.values().stream().allMatch(bool -> !bool);
			return output.stream().map(out -> new Pulse(allHigh, out, this)).collect(Collectors.toList());
		}
		
		@Override
		protected String getName() {
			return name+"(conjunction)";
		}
		
		@Override
		protected void reset() {
			for (Module module : inputIsLow.keySet()) {
				inputIsLow.put(module, true);
			}
			lowPulseCounter = 0;
		}
	}
	
	public static class Broadcaster extends Module {
		
		public Broadcaster(String[] outputs) {
			super(outputs);
		}
		
		@Override
		public void addInput(Module module) {
			input.add(module);
		}
		
		@Override
		protected String getName() {
			return "broadcaster";
		}

		@Override
		protected Collection<Pulse> processPulse(boolean isLow, Day20.Module sourceModule) {
			if (isLow) lowPulseCounter++;
			return output.stream().map(out -> new Pulse(isLow, out, this)).collect(Collectors.toList());
		}

		@Override
		protected void reset() { lowPulseCounter = 01;}
	}

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day20.txt"));

		Broadcaster broadcaster = null;
		Map<String, Module> modulesMap = new HashMap<String, Module>();
		
		for (String string : lines) {
			String[] outputs = string.split(">")[1].trim().split(", ");
			if (string.startsWith("broadcaster")) {
				broadcaster = new Broadcaster(outputs);
			}
			else {
				String name = string.substring(1);
				name = name.split(" " )[0];
				if (string.startsWith("%")) {
					modulesMap.put(name, new FlipFlop(name, outputs));
				}
				else if (string.startsWith("&")) {
					modulesMap.put(name, new Conjunction(name, outputs));
				}
			}
		}
		
		Button button = new Button();
		button.connect(modulesMap, broadcaster);
		if (broadcaster != null) broadcaster.connect(modulesMap, broadcaster);
		for (Module module : new ArrayList<Module>(modulesMap.values())) {
			module.connect(modulesMap, broadcaster);
		}
		
		// Part 1
		long startTime = System.nanoTime();
		AtomicInteger highPulseCounter = new AtomicInteger();
		AtomicInteger lowPulseCounter = new AtomicInteger();
		for (int j=0;j<1000;j++) {
			List<Pulse> pulses = button.generatePulses();
			while (!pulses.isEmpty()) {
				final Pulse pulse = pulses.remove(0);
				if (DEBUG) {
					System.out.println(pulse);
				}
				if (pulse.isLow) lowPulseCounter.incrementAndGet();
				else highPulseCounter.incrementAndGet();
				pulses.addAll(pulse.process());
			}
			if (DEBUG) { System.out.println(); }
		}
		
		long result = ((long)lowPulseCounter.get()) * ((long)highPulseCounter.get());

		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		for (Module module : new ArrayList<Module>(modulesMap.values())) {
			module.reset();
		}
		int press = 0;

		Conjunction conjunctionBeforeRx = (Conjunction) modulesMap.get("rx").input.get(0);
		List<Conjunction> counters = conjunctionBeforeRx.input.stream().filter(m -> m instanceof Conjunction).map(m -> (Conjunction)m).collect(Collectors.toList());
		Map<Conjunction, List<Integer>> collect = counters.stream().collect(Collectors.toMap(Function.identity(), m -> new ArrayList<Integer>()));
		
		while (collect.values().stream().anyMatch(l -> l.isEmpty())) {
			press++;
			List<Pulse> pulses = button.generatePulses();
			while (!pulses.isEmpty()) {
				Collection<Pulse> newPulses = pulses.remove(0).process();

				Map<Module, Boolean> inputLowMap = conjunctionBeforeRx.inputIsLow;
				for (Module module : inputLowMap.keySet()) {
					if (inputLowMap.get(module) == false && collect.get(module).isEmpty()) {
						collect.get(module).add(press);
					}
				}
				
				pulses.addAll(newPulses);
			}
		}

		BigInteger bigResult = collect.values().stream().map(l -> BigInteger.valueOf(l.get(0))).reduce(BigInteger.ONE, (a, b) -> a.multiply(b));
		
		// 247702167614647
		System.out.println("Result part 2 : " + bigResult + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
}
