package org.ebs;

import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class SourceTextSpout extends BaseRichSpout {

    private static final long serialVersionUID = 1;

    private SpoutOutputCollector collector;
    private final String[] sourceText = {
        "text one", "text two", "text three", "text four", "too much text after one", "drop this text"
    };
    private int i = 0;

    // remove template type qualifiers from conf declaration for Storm v1
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        String task = context.getThisComponentId() + " " + context.getThisTaskId();
        System.out.println("----- Started spout task: " + task);
    }

    public void nextTuple() {

        // continuous tuple emission variant
//		this.collector.emit(new Values(sourceText[i]));
//        i = (i + 1) % sourceText.length;

        // limited tuple emission variant
        if (i < sourceText.length) {
            this.collector.emit(new Values(sourceText[i]));
            i++;
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("words"));
    }
}
