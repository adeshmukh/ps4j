package com.github.adeshmukh.ps4j.meter;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredVm;

import com.github.adeshmukh.ps4j.Measure;
import com.github.adeshmukh.ps4j.Meter;
import com.github.adeshmukh.ps4j.Metric;
import com.github.adeshmukh.ps4j.metric.AutoScalingMetric;
import com.github.adeshmukh.ps4j.metric.SimpleMetric;
import com.github.adeshmukh.ps4j.metric.TimeMetric;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Meter implementation that measures data using the hotspot performance counters (introduced for Java5+ VMs).
 *
 * @author adeshmukh
 */
@SuppressWarnings("restriction")
public class HotspotMeter implements Meter {

    private static Function<Monitor, String> GET_NAME = new Function<Monitor, String>() {
        @Override
        public String apply(Monitor m) {
            return m.getName();
        }
    };

    private static final Function<Monitor, Number> GET_LONG_VALUE = new Function<Monitor, Number>() {
        @Override
        public Number apply(Monitor input) {
            return (Number) input.getValue();
        }
    };

    private static final Function<Monitor, Number> GET_NUM_VALUE = new Function<Monitor, Number>() {
        @Override
        public Number apply(Monitor input) {
            return (Number) input.getValue();
        }
    };

    private static final Function<Monitor, String> GET_STRING_VALUE = new Function<Monitor, String>() {
        @Override
        public String apply(Monitor input) {
            String s = String.valueOf(input.getValue());
            return s; // TODO adeshmukh: escape/quote for whitespace
        }
    };

    private static enum StringMetricMonitor {
        vmVersion() {
            private Metric<String> m = new SimpleMetric<String>(name(), "vm version string");

            @Override
            public Metric<String> metric() {
                return m;
            }

            @Override
            public String value(Map<String, String> m) {
                return m.get("java.property.java.vm.version");
            }
        },
        vmName() {
            private Metric<String> m = new SimpleMetric<String>(name(), "vm name");

            @Override
            public Metric<String> metric() {
                return m;
            }

            @Override
            public String value(Map<String, String> m) {
                return m.get("java.property.java.vm.name");
            }
        },
        vmVendor() {
            private Metric<String> m = new SimpleMetric<String>(name(), "vm vendor");

            @Override
            public Metric<String> metric() {
                return m;
            }

            @Override
            public String value(Map<String, String> m) {
                return m.get("java.property.java.vm.vendor");
            }
        };

        public abstract Metric<String> metric();

        public abstract String value(Map<String, String> m);

    }

    private static enum DoubleMetricMonitor {

        edenMax() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "eden gen max");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.0.maxCapacity", -1);
                return Double.valueOf(d);
            }
        },
        edenCap() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "eden gen current capacity");
            @Override
            public Metric<Double> metric() {
                return m;
            }
            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.0.capacity", -1);
                return Double.valueOf(d);
            }
        },
        edenUse() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "current eden usage");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.0.used", -1);
                return Double.valueOf(d);
            }
        },
        sur0Max() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "survivor 1 gen current capacity");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.1.maxCapacity", -1);
                return Double.valueOf(d);
            }
        },
        sur1Cap() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "survivor 1 gen current capacity");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.1.capacity", -1);
                return Double.valueOf(d);
            }
        },
        sur1Use() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "current survivor 1 gen usage");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.1.used", -1);
                return Double.valueOf(d);
            }
        },
        sur2Max() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "survivor 2 max capacity");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.1.maxCapacity", -1);
                return Double.valueOf(d);
            }
        },
        sur2Cap() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "survivor 2 current capacity");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.1.capacity", -1);
                return Double.valueOf(d);
            }
        },
        sur2Use() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "survivor 2 current usage");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.1.used", -1);
                return Double.valueOf(d);
            }
        },
        oldgMax() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "eden gen current capacity");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.1.maxCapacity", -1);
                return Double.valueOf(d);
            }
        },
        oldgCap() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "old gen current capacity");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.1.capacity", -1);
                return Double.valueOf(d);
            }
        },
        oldgUse() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "current old gen usage");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.1.space.0.used", -1);
                return Double.valueOf(d);
            }
        },
        permMax() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "perm gen max");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.2.maxCapacity", -1);
                return Double.valueOf(d);
            }
        },
        permCap() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "perm gen current capacity");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.2.capacity", -1);
                return Double.valueOf(d);
            }
        },
        permUse() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "current perm gen usage");

            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.2.space.0.used", -1);
                return Double.valueOf(d);
            }
        },
        heapCap() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "max heap size");
            @Override
            public Metric<Double> metric() {
                return m;
            }
            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.capacity", -1)
                        + doubleValue(m, "sun.gc.generation.1.capacity", -1)
                        + doubleValue(m, "sun.gc.generation.2.capacity", -1);
                return Double.valueOf(d);
            }
        },
        heapMax() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "current heap capacity");
            @Override
            public Metric<Double> metric() {
                return m;
            }

            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.maxCapacity", -1)
                        + doubleValue(m, "sun.gc.generation.1.maxCapacity", -1)
                        + doubleValue(m, "sun.gc.generation.2.maxCapacity", -1);
                return Double.valueOf(d);
            }
        },
        heapUse() {
            private final Metric<Double> m = new AutoScalingMetric<Double>(name(), "current heap usage");
            @Override
            public Metric<Double> metric() {
                return m;
            }
            @Override
            public Double value(Map<String, Number> m) {
                double d = doubleValue(m, "sun.gc.generation.0.space.0.used", -1)
                        + doubleValue(m, "sun.gc.generation.0.space.1.used", -1)
                        + doubleValue(m, "sun.gc.generation.0.space.2.used", -1)
                        + doubleValue(m, "sun.gc.generation.1.space.0.used", -1)
                        + doubleValue(m, "sun.gc.generation.2.space.0.used", -1);
                return Double.valueOf(d);
            }
        };

        public abstract Metric<Double> metric();

        public abstract Double value(Map<String, Number> m);
    }

    private static enum TimeMetricMonitor {
        clsLoadTime() {
            private Metric<Long> m = new TimeMetric(name(), "Time taken to load classes");
            @Override
            public Metric<Long> metric() {
                return m;
            }
            @Override
            public Long value(Map<String, Number> m) {
                return longValue(m, "sun.cls.time", -1) / longValue(m, "sun.os.hrt.frequency", 1);
            }
        },
        totGcTime() {
            private Metric<Long> m = new TimeMetric(name(), "Total time spent in garbage collection");
            @Override
            public Metric<Long> metric() {
                return m;
            }
            @Override
            public Long value(Map<String, Number> m) {
                return (longValue(m, "sun.gc.collector.0.time", -1) + longValue(m, "sun.gc.collector.1.time", 0))
                        / longValue(m, "sun.os.hrt.frequency", 1);
            }
        },
        fullGcTime() {
            private Metric<Long> m = new TimeMetric(name(), "Time spent for full garbage collection");
            @Override
            public Metric<Long> metric() {
                return m;
            }
            @Override
            public Long value(Map<String, Number> m) {
                return longValue(m, "sun.gc.collector.1.time", -1) / longValue(m, "sun.os.hrt.frequency", 1);
            }
        },
        yngGcTime() {
            private Metric<Long> m = new TimeMetric(name(), "Time spent for young gen garbage collection");
            @Override
            public Metric<Long> metric() {
                return m;
            }
            @Override
            public Long value(Map<String, Number> m) {
                return longValue(m, "sun.gc.collector.0.time", -1) / longValue(m, "sun.os.hrt.frequency", 1);
            }
        },
        timestamp() {
            private Metric<Long> m = new TimeMetric(name(), "Time since start of VM");
            @Override
            public Metric<Long> metric() {
                return m;
            }
            @Override
            public Long value(Map<String, Number> m) {
                return longValue(m, "sun.os.hrt.ticks", -1) / longValue(m, "sun.os.hrt.frequency", 1);
            }
        };

        public abstract Metric<Long> metric();

        public abstract Long value(Map<String, Number> m);
    }

    private static final List<Metric<? extends Comparable<?>>> DOUBLE_METRICS =
            transform(asList(DoubleMetricMonitor.values())
                    , new Function<DoubleMetricMonitor, Metric<? extends Comparable<?>>>() {
                        @Override
                        public Metric<? extends Comparable<?>> apply(DoubleMetricMonitor metricMonitor) {
                            return metricMonitor.metric();
                        }
                    });

    private static final List<Metric<? extends Comparable<?>>> TIME_METRICS =
            transform(asList(TimeMetricMonitor.values())
                    , new Function<TimeMetricMonitor, Metric<? extends Comparable<?>>>() {
                        @Override
                        public Metric<? extends Comparable<?>> apply(TimeMetricMonitor metricMonitor) {
                            return metricMonitor.metric();
                        }
                    });

    private static final List<Metric<? extends Comparable<?>>> STRING_METRICS =
            transform(asList(StringMetricMonitor.values())
                    , new Function<StringMetricMonitor, Metric<? extends Comparable<?>>>() {
                        @Override
                        public Metric<? extends Comparable<?>> apply(StringMetricMonitor metricMonitor) {
                            return metricMonitor.metric();
                        }
                    });

    private static Predicate<Monitor> NUMERIC_VALUE_FILTER = new Predicate<Monitor>() {
        @Override
        public boolean apply(Monitor input) {
            return input.getValue() instanceof Number;
        }
    };

    @Override
    public Collection<Metric<? extends Comparable<?>>> supportedMetrics() {
        Collection<Metric<? extends Comparable<?>>> retval = newArrayList();
        retval.addAll(DOUBLE_METRICS);
        retval.addAll(TIME_METRICS);
        retval.addAll(STRING_METRICS);
        return retval;
    }

    @Override
    public List<Measure<? extends Comparable<?>>> measureData(MonitoredVm vm) {
        List<Measure<? extends Comparable<?>>> retval = new ArrayList<Measure<? extends Comparable<?>>>(30);

        try {
            @SuppressWarnings("unchecked")
            List<Monitor> monitors = vm.findByPattern(".*");

            processTimeMeasures(vm, monitors, retval);
            processMiscMeasures(vm, monitors, retval);
            processNumericMeasures(vm, monitors, retval);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    private void processTimeMeasures(MonitoredVm vm, List<Monitor> monitors, List<Measure<? extends Comparable<?>>> retval) {
        Map<String, Number> m = transformValues(uniqueIndex(monitors, GET_NAME), GET_LONG_VALUE);

        for (TimeMetricMonitor mm : TimeMetricMonitor.values()) {
            Long d = mm.value(m);
            retval.add(mm.metric().newMeasure(d));
        }
    }

    private void processMiscMeasures(MonitoredVm vm, List<Monitor> monitors, List<Measure<? extends Comparable<?>>> retval) {
        Map<String, String> m = transformValues(uniqueIndex(monitors, GET_NAME), GET_STRING_VALUE);

        for (StringMetricMonitor mm : StringMetricMonitor.values()) {
            String d = mm.value(m);
            retval.add(mm.metric().newMeasure(d));
        }
    }

    private void processNumericMeasures(MonitoredVm vm, List<Monitor> monitors, List<Measure<? extends Comparable<?>>> retval) {
        Map<String, Number> m = transformValues(uniqueIndex(filter(monitors, NUMERIC_VALUE_FILTER), GET_NAME), GET_NUM_VALUE);

        for (DoubleMetricMonitor mm : DoubleMetricMonitor.values()) {
            Double d = mm.value(m);
            retval.add(mm.metric().newMeasure(d));
        }
    }

    // retval.add(new SimpleMeasure<Integer>("pid", valueOf(vm.getVmIdentifier().getLocalVmId())));
    //
    // retval.add(clsLoad(m));
    // retval.add(clsUnload(m));
    //
    // retval.add(threadsLive(m));
    // retval.add(threadsLivePeak(m));
    // retval.add(threadsDaemon(m));
    // retval.add(threadsStarted(m));
    //
    // retval.add(clsLoadTime(m));
    //
    // retval.add(yngGcTime(m));
    // retval.add(yngGcCnt(m));
    // retval.add(fullGcTime(m));
    // retval.add(fullGcCnt(m));
    // retval.add(totGcTime(m));
    //
    // retval.add(timestamp(m));


    // private AutoScalingMeasure<Long> clsUnload(Map<String, Number> m) {
    // long l = longValue(m, "java.cls.loadedClasses", 0) + longValue(m, "java.cls.sharedLoadedClasses", 0);
    // return new AutoScalingMeasure<Long>("clsLoad", l);
    // }
    //
    // private SimpleMeasure<? extends Comparable<?>> clsLoad(Map<String, Number> m) {
    // long l = longValue(m, "java.cls.unloadedClasses", 0) + longValue(m, "java.cls.sharedUnloadedClasses", 0);
    // return new AutoScalingMeasure<Long>("clsUnld", l);
    // }
    //
    // private AutoScalingMeasure<Long> threadsStarted(Map<String, Number> m) {
    // long l = longValue(m, "java.threads.started", -1);
    // return new AutoScalingMeasure<Long>("thrstart", l);
    // }
    //
    // private AutoScalingMeasure<Long> threadsDaemon(Map<String, Number> m) {
    // long l = longValue(m, "java.threads.daemon", -1);
    // return new AutoScalingMeasure<Long>("thrdaem", l);
    // }
    //
    // private AutoScalingMeasure<Long> threadsLivePeak(Map<String, Number> m) {
    // long l = longValue(m, "java.threads.livePeak", -1);
    // return new AutoScalingMeasure<Long>("thrlvpk", l);
    // }
    //
    // private AutoScalingMeasure<Long> threadsLive(Map<String, Number> m) {
    // long l = longValue(m, "java.threads.live", -1);
    // return new AutoScalingMeasure<Long>("thrlive", l);
    // }
    //
    // private SimpleMeasure<String> vmVersion(Map<String, Comparable<?>> m) {
    // String s = (String) m.get("java.property.java.vm.version");
    // return new SimpleMeasure<String>("vmversion", s);
    // }
    //
    //
    // private AutoScalingMeasure<Long> fullGcCnt(Map<String, Number> m) {
    // long l = longValue(m, "sun.gc.collector.1.invocations", -1);
    // return new AutoScalingMeasure<Long>("fullGcCnt", l);
    // }
    //
    // private AutoScalingMeasure<Long> yngGcCnt(Map<String, Number> m) {
    // long l = longValue(m, "sun.gc.collector.0.invocations", -1);
    // return new AutoScalingMeasure<Long>("youngGcCnt", l);
    // }
    //

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
