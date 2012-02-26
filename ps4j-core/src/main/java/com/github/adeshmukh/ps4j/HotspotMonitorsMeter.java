package com.github.adeshmukh.ps4j;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.Integer.valueOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredVm;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Meter implementation that measures data using the hotspot performance counters (introduced for Java5+ VMs).
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class HotspotMonitorsMeter implements Meter {

    private static Function<Monitor, String> KEY_FUNCTION = new Function<Monitor, String>() {
        @Override
        public String apply(Monitor m) {
            return m.getName();
        }
    };

    private static final Function<Monitor, Number> NUMBER_VALUE_XFORMER = new Function<Monitor, Number>() {
        @Override
        public Number apply(Monitor input) {
            return (Number) input.getValue();
        }
    };

    private static final Function<Monitor, Comparable<?>> VALUE_XFORMER = new Function<Monitor, Comparable<?>>() {
        @Override
        public Comparable<?> apply(Monitor input) {
            return (Comparable<?>) input.getValue();
        }
    };

    private static Predicate<Monitor> NUMERIC_VALUE_FILTER = new Predicate<Monitor>() {
        @Override
        public boolean apply(Monitor input) {
            return input.getValue() instanceof Number;
        }
    };

    @Override
    public List<Measure<? extends Comparable<?>>> measures(MonitoredVm vm) {
        List<Measure<? extends Comparable<?>>> retval = new ArrayList<Measure<? extends Comparable<?>>>(30);

        try {
            @SuppressWarnings("unchecked")
            List<Monitor> monitors = vm.findByPattern(".*");

            processSimpleMeasures(vm, monitors, retval);
            processNumericMeasures(vm, monitors, retval);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    private void processSimpleMeasures(MonitoredVm vm, List<Monitor> monitors, List<Measure<? extends Comparable<?>>> retval) {

        Map<String, Comparable<?>> m = transformValues(uniqueIndex(monitors, KEY_FUNCTION), VALUE_XFORMER);

        retval.add(vmVersion(m));
    }

    private void processNumericMeasures(MonitoredVm vm, List<Monitor> monitors, List<Measure<? extends Comparable<?>>> retval) {

        Map<String, Number> m = transformValues(uniqueIndex(filter(monitors, NUMERIC_VALUE_FILTER), KEY_FUNCTION), NUMBER_VALUE_XFORMER);

        retval.add(new SimpleMeasure<Integer>("pid", valueOf(vm.getVmIdentifier().getLocalVmId())));

        retval.add(heapMax(m));
        retval.add(heapCap(m));
        retval.add(heapUse(m));

        retval.add(permMax(m));
        retval.add(permCap(m));
        retval.add(permUse(m));

        retval.add(oldgMax(m));
        retval.add(oldgCap(m));
        retval.add(oldgUse(m));

        retval.add(sur1Max(m));
        retval.add(sur1Cap(m));
        retval.add(sur1Use(m));

        retval.add(sur0Max(m));
        retval.add(sur0Cap(m));
        retval.add(sur0Use(m));

        retval.add(edenMax(m));
        retval.add(edenCap(m));
        retval.add(edenUse(m));

        retval.add(clsLoad(m));
        retval.add(clsUnload(m));

        retval.add(threadsLive(m));
        retval.add(threadsLivePeak(m));
        retval.add(threadsDaemon(m));
        retval.add(threadsStarted(m));

        retval.add(clsLoadTime(m));

        retval.add(yngGcTime(m));
        retval.add(yngGcCnt(m));
        retval.add(fullGcTime(m));
        retval.add(fullGcCnt(m));
        retval.add(totGcTime(m));

        retval.add(timestamp(m));
    }

    private AutoScalingMeasure<Long> clsUnload(Map<String, Number> m) {
        long l = longValue(m, "java.cls.loadedClasses", 0) + longValue(m, "java.cls.sharedLoadedClasses", 0);
        return new AutoScalingMeasure<Long>("clsLoad", l);
    }

    private SimpleMeasure<? extends Comparable<?>> clsLoad(Map<String, Number> m) {
        long l = longValue(m, "java.cls.unloadedClasses", 0) + longValue(m, "java.cls.sharedUnloadedClasses", 0);
        return new AutoScalingMeasure<Long>("clsUnld", l);
    }

    private TimeMeasure clsLoadTime(Map<String, Number> m) {
        long t = longValue(m, "sun.cls.time", -1) / longValue(m, "sun.os.hrt.frequency", 1);
        return new TimeMeasure("timeClsld", t);
    }

    private AutoScalingMeasure<Double> edenCap(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.0.capacity", -1);
        return new AutoScalingMeasure<Double>("edenCap", d);
    }

    private AutoScalingMeasure<Double> edenMax(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.0.maxCapacity", -1);
        return new AutoScalingMeasure<Double>("edenMax", d);
    }

    private AutoScalingMeasure<Double> edenUse(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.0.used", -1);
        return new AutoScalingMeasure<Double>("edenUse", d);
    }

    private AutoScalingMeasure<Double> heapCap(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.capacity", -1)
                + doubleValue(m, "sun.gc.generation.1.capacity", -1)
                + doubleValue(m, "sun.gc.generation.2.capacity", -1);
        return new AutoScalingMeasure<Double>("heapCap", d);
    }

    private AutoScalingMeasure<Double> heapMax(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.maxCapacity", -1)
                + doubleValue(m, "sun.gc.generation.1.maxCapacity", -1)
                + doubleValue(m, "sun.gc.generation.2.maxCapacity", -1);
        return new AutoScalingMeasure<Double>("heapMax", d);
    }

    private AutoScalingMeasure<Double> heapUse(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.0.used", -1)
                + doubleValue(m, "sun.gc.generation.0.space.1.used", -1)
                + doubleValue(m, "sun.gc.generation.0.space.2.used", -1)
                + doubleValue(m, "sun.gc.generation.1.space.0.used", -1)
                + doubleValue(m, "sun.gc.generation.2.space.0.used", -1);
        return new AutoScalingMeasure<Double>("heapUse", d);
    }

    private AutoScalingMeasure<Double> oldgCap(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.1.capacity", -1);
        return new AutoScalingMeasure<Double>("oldgCap", d);
    }

    private AutoScalingMeasure<Double> oldgMax(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.1.maxCapacity", -1);
        return new AutoScalingMeasure<Double>("oldgMax", d);
    }

    private AutoScalingMeasure<Double> oldgUse(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.1.space.0.used", -1);
        return new AutoScalingMeasure<Double>("oldgUse", d);
    }

    private AutoScalingMeasure<Double> permCap(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.2.capacity", -1);
        return new AutoScalingMeasure<Double>("permCap", d);
    }

    private AutoScalingMeasure<Double> permMax(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.2.maxCapacity", -1);
        return new AutoScalingMeasure<Double>("permMax", d);
    }

    private AutoScalingMeasure<Double> permUse(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.2.space.0.used", -1);
        return new AutoScalingMeasure<Double>("permUse", d);
    }

    private AutoScalingMeasure<Double> sur0Cap(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.1.capacity", -1);
        return new AutoScalingMeasure<Double>("sur0Cap", d);
    }

    private AutoScalingMeasure<Double> sur0Max(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.1.maxCapacity", -1);
        return new AutoScalingMeasure<Double>("sur0Max", d);
    }

    private AutoScalingMeasure<Double> sur0Use(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.1.used", -1);
        return new AutoScalingMeasure<Double>("sur0Use", d);
    }

    private AutoScalingMeasure<Double> sur1Cap(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.2.capacity", -1);
        return new AutoScalingMeasure<Double>("sur1Cap", d);
    }

    private AutoScalingMeasure<Double> sur1Max(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.2.maxCapacity", -1);
        return new AutoScalingMeasure<Double>("sur1Max", d);
    }

    private AutoScalingMeasure<Double> sur1Use(Map<String, Number> m) {
        double d = doubleValue(m, "sun.gc.generation.0.space.2.used", -1);
        return new AutoScalingMeasure<Double>("sur1Use", d);
    }

    private AutoScalingMeasure<Long> threadsStarted(Map<String, Number> m) {
        long l = longValue(m, "java.threads.started", -1);
        return new AutoScalingMeasure<Long>("thrstart", l);
    }

    private AutoScalingMeasure<Long> threadsDaemon(Map<String, Number> m) {
        long l = longValue(m, "java.threads.daemon", -1);
        return new AutoScalingMeasure<Long>("thrdaem", l);
    }

    private AutoScalingMeasure<Long> threadsLivePeak(Map<String, Number> m) {
        long l = longValue(m, "java.threads.livePeak", -1);
        return new AutoScalingMeasure<Long>("thrlvpk", l);
    }

    private AutoScalingMeasure<Long> threadsLive(Map<String, Number> m) {
        long l = longValue(m, "java.threads.live", -1);
        return new AutoScalingMeasure<Long>("thrlive", l);
    }

    private SimpleMeasure<String> vmVersion(Map<String, Comparable<?>> m) {
        String s = (String) m.get("java.property.java.vm.version");
        return new SimpleMeasure<String>("vmversion", s);
    }

    private TimeMeasure totGcTime(Map<String, Number> m) {
        long t = (longValue(m, "sun.gc.collector.0.time", -1) + longValue(m, "sun.gc.collector.1.time", 0))
                / longValue(m, "sun.os.hrt.frequency", 1);
        return new TimeMeasure("totGcTime", t);
    }

    private AutoScalingMeasure<Long> fullGcCnt(Map<String, Number> m) {
        long l = longValue(m, "sun.gc.collector.1.invocations", -1);
        return new AutoScalingMeasure<Long>("fullGcCnt", l);
    }

    private TimeMeasure fullGcTime(Map<String, Number> m) {
        long t = longValue(m, "sun.gc.collector.1.time", -1) / longValue(m, "sun.os.hrt.frequency", 1);
        return new TimeMeasure("fullGcTime", t);
    }

    private AutoScalingMeasure<Long> yngGcCnt(Map<String, Number> m) {
        long l = longValue(m, "sun.gc.collector.0.invocations", -1);
        return new AutoScalingMeasure<Long>("youngGcCnt", l);
    }

    private TimeMeasure yngGcTime(Map<String, Number> m) {
        long t = longValue(m, "sun.gc.collector.0.time", -1) / longValue(m, "sun.os.hrt.frequency", 1);
        return new TimeMeasure("yngGcTime", t);
    }

    private TimeMeasure timestamp(Map<String, Number> m) {
        long t = longValue(m, "sun.os.hrt.ticks", -1) / longValue(m, "sun.os.hrt.frequency", 1);
        return new TimeMeasure("timestamp", t);
    }

    private static double doubleValue(Map<String, Number> m, String key, double defolt) {
        try {
            Number n = m.get(key);
            return n.doubleValue();
        } catch (Exception e) {
        }
        return defolt;
    }

    private static long longValue(Map<String, Number> m, String key, int defolt) {
        try {
            Number n = m.get(key);
            return n.longValue();
        } catch (Exception e) {
        }
        return defolt;
    }

}
