package com.buschmais.jqassistant.plugin.yaml.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.yaml.api.model.YAMLDocumentDescriptor;
import com.buschmais.jqassistant.plugin.yaml.api.model.YAMLFileDescriptor;
import com.buschmais.jqassistant.plugin.yaml.api.model.YAMLKeyDescriptor;
import com.buschmais.jqassistant.plugin.yaml.api.model.YAMLValueDescriptor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static com.buschmais.jqassistant.plugin.yaml.impl.scanner.util.StringValueMatcher.hasValue;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class YAMLFileScannerPluginIT extends AbstractPluginIT {
    @Test
    public void scanReturnsFileDescriptorWithCorrectFileName() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/simple-key-value-pair.yaml");

        Scanner scanner = getScanner();
        YAMLFileDescriptor file = scanner.scan(yamlFile, yamlFile.getAbsolutePath(), null);

        assertThat("Scanner must be able to scan the resource and to return a descriptor.",
                   file, notNullValue());

        assertThat(file.getFileName(), Matchers.notNullValue());
        assertThat(file.getFileName(), endsWith("probes/valid/simple-key-value-pair.yaml"));

        store.commitTransaction();
    }


    @Test
    public void scanSimpleKeyValuePairYAML() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/simple-key-value-pair.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLFileDescriptor> fileDescriptors =
             query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/probes/valid/simple-key-value-pair.yaml' RETURN f")
                .getColumn("f");

        assertThat(fileDescriptors, hasSize(1));

        YAMLFileDescriptor file = fileDescriptors.get(0);
        assertThat(file.getDocuments(), hasSize(1));

        YAMLDocumentDescriptor document = file.getDocuments().get(0);

        assertThat(document.getValues(), hasSize(0));
        assertThat(document.getKeys(), hasSize(1));

        YAMLKeyDescriptor key = document.getKeys().get(0);

        assertThat(key.getName(), equalTo("key"));
        assertThat(key.getFullQualifiedName(), equalTo("key"));
        assertThat(key.getValues(), hasSize(1));
        assertThat(key.getPosition(), equalTo(0));

        YAMLValueDescriptor value = key.getValues().get(0);

        assertThat(value.getValue(), equalTo("value"));
        assertThat(value.getPosition(), equalTo(0));

        store.commitTransaction();
    }


    @Test
    public void scanTwoSimpleKeyValuePairsYAML() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/two-simple-key-value-pairs.yaml");


        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLFileDescriptor> fileDescriptors =
             query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/two-simple-key-value-pairs.yaml' RETURN f")
                  .getColumn("f");

        assertThat(fileDescriptors, hasSize(1));

        YAMLFileDescriptor file = fileDescriptors.get(0);
        assertThat(file.getDocuments(), hasSize(1));

        YAMLDocumentDescriptor document = file.getDocuments().get(0);

        assertThat(document.getValues(), hasSize(0));
        assertThat(document.getKeys(), hasSize(2));

        YAMLKeyDescriptor keyA = document.getKeys().get(0);

        assertThat(keyA.getName(), equalTo("keyA"));
        assertThat(keyA.getPosition(), equalTo(0));
        assertThat(keyA.getFullQualifiedName(), equalTo("keyA"));
        assertThat(keyA.getValues(), hasSize(1));

        YAMLValueDescriptor valueOfKeyA = keyA.getValues().get(0);

        assertThat(valueOfKeyA.getValue(), equalTo("valueA"));
        assertThat(valueOfKeyA.getPosition(), equalTo(0));


        store.commitTransaction();
    }


    @Test
    public void scanSimpleListYAML() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/valid/simple-list.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLFileDescriptor> fileDescriptors =
             query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/simple-list.yaml' RETURN f").getColumn("f");

        assertThat(fileDescriptors, hasSize(1));
        assertThat(fileDescriptors.get(0).getDocuments(), hasSize(1));

        YAMLDocumentDescriptor documentDescriptor = fileDescriptors.get(0).getDocuments().get(0);

        assertThat(documentDescriptor.getKeys(), hasSize(1));
        assertThat(documentDescriptor.getValues(), empty());

        YAMLKeyDescriptor keyDescriptor = documentDescriptor.getKeys().get(0);

        assertThat(keyDescriptor.getName(), equalTo("alist"));
        assertThat(keyDescriptor.getValues(), hasSize(3));
        assertThat(keyDescriptor.getKeys(), empty());
        assertThat(keyDescriptor.getValues(), containsInAnyOrder(hasValue("a"), hasValue("b"),
                                                                 hasValue("c")));


        store.commitTransaction();
    }

    @Test
    public void scanSequenceOfScalarsYAML() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/yamlspec/1.1/sec-2.1-example-2.1-sequence-of-scalars.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLFileDescriptor> fileDescriptors =
             query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/sec-2.1-example-2.1-sequence-of-scalars.yaml' RETURN f")
                  .getColumn("f");

        assertThat(fileDescriptors, hasSize(1));
        List<YAMLDocumentDescriptor> documents = fileDescriptors.get(0).getDocuments();

        assertThat(documents, hasSize(1));

        YAMLDocumentDescriptor document = documents.get(0);

        assertThat(document.getKeys(), empty());
        assertThat(document.getValues(), hasSize(3));

        for (YAMLValueDescriptor d : document.getValues()) {

            System.out.println(d.getValue());
        }

        System.out.println();

             assertThat(document.getValues(), containsInAnyOrder(hasValue("Mark McGwire"),
                                                                 hasValue("Sammy Sosa"),
                                                                 hasValue("Ken Griffey")));

        store.commitTransaction();
    }

    @Test
    @Ignore
    public void scanScalarsOfScalarsYAML() {
        Assert.fail("Not implemented yet!");
//        "/probes/yamlspec/1.1/sec-2.1-example-2.2-scalars-of-scalars.yaml"
    }

    @Test
    public void scanMappingScalarsToSequencesYAML() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/yamlspec/1.1/sec-2.1-example-2.3-mapping-scalars-to-sequences.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLFileDescriptor> fileDescriptors =
             query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/sec-2.1-example-2.3-mapping-scalars-to-sequences.yaml' RETURN f")
                  .getColumn("f");

        YAMLFileDescriptor fileDescriptor = fileDescriptors.get(0);

        assertThat(fileDescriptor.getDocuments(), hasSize(1));

        YAMLDocumentDescriptor docDescriptor = fileDescriptor.getDocuments().get(0);


        YAMLKeyDescriptor keyDescriptor1 = docDescriptor.getKeys().get(0);

        assertThat(keyDescriptor1.getName(), equalTo("american"));
        assertThat(keyDescriptor1.getFullQualifiedName(), equalTo("american"));
        assertThat(keyDescriptor1.getValues(), hasSize(3));
        assertThat(keyDescriptor1.getKeys(), empty());
        assertThat(keyDescriptor1.getValues(), containsInAnyOrder(hasValue("Boston Red Sox"),
                                                                  hasValue("New York Yankees"),
                                                                  hasValue("Detroit Tigers")));

        YAMLKeyDescriptor keyDescriptor2 = docDescriptor.getKeys().get(1);

        assertThat(keyDescriptor2.getName(), equalTo("national"));
        assertThat(keyDescriptor2.getFullQualifiedName(), equalTo("national"));
        assertThat(keyDescriptor2.getValues(), hasSize(3));
        assertThat(keyDescriptor2.getKeys(), empty());
        assertThat(keyDescriptor2.getValues(), containsInAnyOrder(hasValue("New York Mets"),
                                                                  hasValue("Chicago Cubs"),
                                                                  hasValue("Atlanta Braves")));
        store.commitTransaction();
    }

    @Test
    @Ignore
    public void scanSequenceOfMappingsYAML() {
        Assert.fail("Not implemented yet!");
//             {"/probes/yamlspec/1.1/sec-2.1-example-2.4-sequence-of-mappings.yaml"},
    }

    @Test
    public void scanSequenceOfSequencesYAML() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/yamlspec/1.1/sec-2.1-example-2.5-sequence-of-sequences.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLFileDescriptor> fileDescriptors = query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/1.1/sec-2.1-example-2.5-sequence-of-sequences.yaml' RETURN f")
             .getColumn("f");

        assertThat(fileDescriptors, hasSize(1));

        YAMLFileDescriptor file = fileDescriptors.get(0);

        assertThat(file.getDocuments(), hasSize(1));

        YAMLDocumentDescriptor document = file.getDocuments().get(0);

        assertThat(document.getKeys(), empty());
        assertThat(document.getValues(), hasSize(3));

        YAMLValueDescriptor firstSequence = document.getValues().get(0);
        YAMLValueDescriptor secondSequence = document.getValues().get(1);
        YAMLValueDescriptor thirdSequence = document.getValues().get(2);

        assertThat(firstSequence.getValue(), nullValue());
        assertThat(firstSequence.getValues(), hasSize(3));
        assertThat(firstSequence.getValues().get(0).getValue(), equalTo("name"));
        assertThat(firstSequence.getValues().get(1).getValue(), equalTo("hr"));
        assertThat(firstSequence.getValues().get(2).getValue(), equalTo("avg"));

        assertThat(secondSequence.getValue(), nullValue());
        assertThat(secondSequence.getValues(), hasSize(3));

        assertThat(thirdSequence.getValue(), CoreMatchers.nullValue());
        assertThat(thirdSequence.getValues().get(0).getValue(), equalTo("Sammy Sosa"));
        assertThat(thirdSequence.getValues().get(1).getValue(), equalTo("63"));
        assertThat(thirdSequence.getValues().get(2).getValue(), equalTo("0.288"));

        store.commitTransaction();
    }

    @Test
    public void scanMappingOfMappingsYAML() {
        store.beginTransaction();

        File yamlFile = new File(getClassesDirectory(YAMLFileScannerPluginValidFileSetIT.class),
                                 "/probes/yamlspec/1.1/sec-2.1-example-2.6-mapping-of-mappings.yaml");

        getScanner().scan(yamlFile, yamlFile.getAbsolutePath(), null);

        List<YAMLFileDescriptor> fileDescriptors =
             query("MATCH (f:YAML:File) WHERE f.fileName=~'.*/1.1/sec-2.1-example-2.6-mapping-of-mappings.yaml' RETURN f")
                  .getColumn("f");

        assertThat(fileDescriptors, hasSize(1));

        YAMLFileDescriptor fileDescriptor = fileDescriptors.get(0);

        assertThat(fileDescriptor.getDocuments(), hasSize(1));

        YAMLDocumentDescriptor document = fileDescriptor.getDocuments().get(0);

        print(document);
        assertThat(document.getKeys(), hasSize(2));
        assertThat(document.getValues(), empty());

        YAMLKeyDescriptor keyA = document.getKeys().get(0);

        assertThat(keyA.getKeys(), hasSize(2));
        assertThat(keyA.getName(), equalTo("Mark McGwire"));
        assertThat(keyA.getValues(), empty());

        YAMLKeyDescriptor keyA1 = keyA.getKeys().get(0);


        System.out.println(keyA1.getName());
        System.out.println(keyA1.getFullQualifiedName());

        assertThat(keyA1.getName(), equalTo("hr"));
        assertThat(keyA1.getFullQualifiedName(), CoreMatchers.equalTo("Mark McGwire.hr"));
        assertThat(keyA1.getKeys(), empty());
        assertThat(keyA1.getValues(), hasSize(1));
        assertThat(keyA1.getValues(), contains(hasValue("65")));


        YAMLKeyDescriptor keyA2 = keyA.getKeys().get(1);

        System.out.println(keyA2.getName());
        System.out.println(keyA2.getFullQualifiedName());

        assertThat(keyA2.getName(), equalTo("avg"));
        assertThat(keyA2.getFullQualifiedName(), CoreMatchers.equalTo("Mark McGwire.avg"));
        assertThat(keyA2.getKeys(), empty());
        assertThat(keyA2.getValues(), hasSize(1));
        assertThat(keyA2.getValues(), contains(hasValue("0.278")));

        //---

        YAMLKeyDescriptor keyB = document.getKeys().get(1);

        assertThat(keyB.getName(), CoreMatchers.equalTo("Sammy Sosa"));
        assertThat(keyB.getValues(), empty());
        assertThat(keyB.getKeys(), hasSize(2));

        YAMLKeyDescriptor keyB2 = keyB.getKeys().get(1);

        assertThat(keyB2.getFullQualifiedName(), CoreMatchers.equalTo("Sammy Sosa.avg"));
        assertThat(keyB2.getValues(), hasSize(1));
        assertThat(keyB2.getValues(), contains(hasValue("0.288")));

        store.commitTransaction();
    }

    @Test
    @Ignore
    public void scanlayByPlayYAML() {
        Assert.fail("Not implemented yet!");
//             {"/probes/yamlspec/1.1/sec-2.2-example-2.8-play-by-play.yaml"},
    }

    @Test
    @Ignore
    public void scanTwoDocumenstsInAStreamYAML() {
        Assert.fail("Not implemented yet!");
//             {"/probes/yamlspec/1.1/sec-2.2-example-2.7-two-documensts-in-a-stream.yaml"},
    }

    @Test
    @Ignore
    public void scanSingleDocumentWithCommentsYAML() {
        Assert.fail("Not implemented yet!");
//             {"/probes/yamlspec/1.1/sec-2.2-example-2.9-single-document-with-comments.yaml"},
    }

//    @Test
//    public void scan//             {"/probes/yamlspec/1.1/sec-2.2-example-2.10-node-for-sammy-sosa-twice.yaml"},
//             {"/probes/yamlspec/1.1/sec-2.2-example-2.10-node-for-sammy-sosa-twice.yaml"},

//    @Test
//    public void scan//             {"/probes/yamlspec/1.1/sec-2.2-example-2.11-mapping-betweend-sequences.yaml"},
//             {"/probes/yamlspec/1.1/sec-2.2-example-2.11-mapping-betweend-sequences.yaml"},

//    @Test
//    public void scan//             {"/probes/yamlspec/1.1/sec-2.2-example-2.12-in-line-nested-mapping.yaml"},
//             {"/probes/yamlspec/1.1/sec-2.2-example-2.12-in-line-nested-mapping.yaml"},

//    @Test
//    public void scan//             {"/probes/yamlspec/1.1/sec-2.3-example-2.13-in-literals-newlines-preserved.yaml"},
//             {"/probes/yamlspec/1.1/sec-2.3-example-2.13-in-literals-newlines-preserved.yaml"},

//    @Test
//    public void scan//             {"/probes/yamlspec/1.1/sec-2.3-example-2.14-in-the-plain-scalar-newline-as-spaces.yaml"},
//             {"/probes/yamlspec/1.1/sec-2.3-example-2.14-in-the-plain-scalar-newline-as-spaces.yaml"},

//    @Test
//    public void scan//             {"/probes/yamlspec/1.1/sec-2.3-example-2.15-folded-newlines-are-preserved.yaml"},
//             {"/probes/yamlspec/1.1/sec-2.3-example-2.15-folded-newlines-are-preserved.yaml"},

    @Test
    @Ignore
    public void scanIndentationDeterminesScopeYAML() {
//             {"/probes/yamlspec/1.1/sec-2.3-example-2.16-indentation-determines-scope.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanQuotedScalarsYAML() {
//             {"/probes/yamlspec/1.1/sec-2.3-example-2.17-quoted-scalars.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanMultiLineFlowScalarsYAML() {
//             {"/probes/yamlspec/1.1/sec-2.3-example-2.18-multi-line-flow-scalars.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanIntegersYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.19-integers.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanFloatingPointYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.20-floating-point.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanMiscYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.21-misc.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanTimestampsYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.22-timestamps.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanVariousExplicitTagsYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.23-various-explicit-tags.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test @Ignore
    public void scanGlobalTagsYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.24-global-tags.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanUnorderedSetsYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.25-unordered-sets.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanOrderedMappingsYAML() {
//             {"/probes/yamlspec/1.1/sec-2.4-example-2.26-ordered-mappings.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanExampleInvoiceYAML() {
//             {"/probes/yamlspec/1.1/sec-2.5-example-2.27-invoice.yaml"},
        Assert.fail("Not implemented yet!");
    }

    @Test
    @Ignore
    public void scanLogFileYAML() {
//             {"/probes/yamlspec/1.1/sec-2.5-example-2.28-log-file.yaml"},
        Assert.fail("Not implemented yet!");
    }

    int indent = 0;
    public void print(YAMLDocumentDescriptor d) {
        for (YAMLKeyDescriptor keyDescriptor : d.getKeys()) {
            printKey(keyDescriptor);
        }
    }

    private void printKey(YAMLKeyDescriptor keyDescriptor) {
        i();
        System.out.println(keyDescriptor.getFullQualifiedName());

        for (YAMLValueDescriptor valueDescriptor : keyDescriptor.getValues()) {
            printVal(valueDescriptor);
        }

    }

    private void printVal(YAMLValueDescriptor valueDescriptor) {
        i();
        System.out.print("- ");
        System.out.println(valueDescriptor.getValue());
    }

    private void i() {
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }

    }
}