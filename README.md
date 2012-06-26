PS4J - Java Process Status
==========================

A utility that lists memory and other process information for Hotspot VMs.

Prerequisites
=============
JDK 1.6+

Build
=====
Say $PS4J_SRC refers to the location where the source was checked out:
>  cd $PS4J_SRC/ps4j-cli
> mvn assembly:assembly

This should generate the distro: $PS4J_SRC/ps4j-cli/target/ps4j-cli-1.0.0-SNAPSHOT-bin.tar.gz

Usage
=====
Untar the distro to installation location (e.g. /usr/local/)
> export INSTALL_BASE=/usr/local/  # Exporting vars not strictly required
> tar -zx -C $INSTALL_BASE -f $PS4J_SRC/ps4j-cli/target/ps4j-cli-1.0.0-SNAPSHOT-bin.tar.gz
> $INSTALL_BASE/ps4j-cli-1.0.0-SNAPSHOT/bin/ps4j --help

