#!/bin/bash
java -Xmx1024M -classpath bin:lib/jfreechart-1.0.11.jar:lib/lingpipe-2.3.0.jar:lib/riverlayout.jar:lib/stanford-postagger-2010-05-26.jar:lib/trove.jar:lib/weka.jar:lib/XMLBoss.jar:lib/xmlparserv2.jar:lib/yeritools.jar edu.cmu.side.PredictionShell $@

