package org.yazhen.dynamodb.extract.dynamodb.document.parser;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the {@link DynamoDBDocumentParser} interface. </p>
 * This parser has identical behavior as the AWS Data Pipeline's DynamoDBS3Extract jobs. </p>
 * Documents will be extracted into DynamoDB JSON format (with type metadata). </p>
 */
public class DynamoDBJSONFormatDocumentParser implements DynamoDBDocumentParser {
    /**
     * Parses the supplied {@link ScanResult} instance to JSON strings with DynamoDB document type metadata. </p>
     * For example: {"attribute1" : {"s": "value"}} </p>
     * The type metadata is set to use lower case to be consistent with the behavior of AWS Data Pipeline.
     * {@inheritDoc}
     */
    @Override
    public List<String> parseToListOfString(ScanResult scanResult) throws UnsupportedEncodingException {
        if (scanResult == null || scanResult.getCount() <= 0) {
            return new ArrayList<String>();
        }

        final ArrayNode convertedJsonArray = parseToArrayNode(scanResult);

        final List<String> contents = new ArrayList<>();

        convertedJsonArray.elements().forEachRemaining(t -> {
            contents.add(t.toString());
        });

        return contents;
    }

    /**
     * Parses the supplied {@link ScanResult} into an {@link ArrayNode} instance.
     *
     * @param scanResult A non-null instance of {@link ScanResult} from {@link AmazonDynamoDB#scan} call
     * @return A non-null instance of {@link ArrayNode} that is parsed from the supplied {@link ScanResult}
     */
    ArrayNode parseToArrayNode(final ScanResult scanResult) throws UnsupportedEncodingException {
        final List<Map<String, AttributeValue>> items = scanResult.getItems();

        final ArrayNode array = JsonNodeFactory.instance.arrayNode();

        for (final Map<String, AttributeValue> item : items) {
            array.add(parseAttribute(item, 0));
        }

        return array;
    }

    /**
     * Parses a given {@link AttributeValue} into an {@link ObjectNode}.
     *
     * @param depth Current depth from the root document structure.
     * @return A non-null instance of {@link ObjectNode} that is parsed from the supplied {@link AttributeValue}
     */
    ObjectNode parseAttribute(final Map<String, AttributeValue> item, final int depth) throws UnsupportedEncodingException {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();

        for (final Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            node.set(entry.getKey(), getJsonNode(entry.getValue(), depth + 1));
        }
        return node;
    }

    private JsonNode getJsonNode(final AttributeValue av, final int depth) throws UnsupportedEncodingException {
        if (av.getS() != null) {
            final ObjectNode node = JsonNodeFactory.instance.objectNode();

            node.set("s", JsonNodeFactory.instance.textNode(av.getS()));

            return node;
        } else if (av.getN() != null) {
            try {
                final ObjectNode node = JsonNodeFactory.instance.objectNode();

                node.set("n", JsonNodeFactory.instance.numberNode(Integer.parseInt(av.getN())));

                return node;
            } catch (final NumberFormatException e) {
                // Not an integer
                try {
                    final ObjectNode node = JsonNodeFactory.instance.objectNode();

                    node.set("n", JsonNodeFactory.instance.numberNode(Float.parseFloat(av.getN())));

                    return node;
                } catch (final NumberFormatException e2) {
                    // Not a number
                    throw new RuntimeException(e.getMessage());
                }
            }
        } else if (av.getBOOL() != null) {
            final ObjectNode node = JsonNodeFactory.instance.objectNode();

            node.set("bool", JsonNodeFactory.instance.booleanNode(av.getBOOL()));

            return node;
        } else if (av.getNULL() != null) {
            final ObjectNode node = JsonNodeFactory.instance.objectNode();

            node.set("null", JsonNodeFactory.instance.nullNode());

            return node;
        } else if (av.getL() != null) {
            final ObjectNode node = JsonNodeFactory.instance.objectNode();

            node.set("l", listToJsonArray(av.getL(), depth));

            return node;
        } else if (av.getM() != null) {
            return parseAttribute(av.getM(), depth);
        } else if (av.getB() != null) {
            final ObjectNode node = JsonNodeFactory.instance.objectNode();

            node.set("b", JsonNodeFactory.instance.binaryNode(av.getB().array()));

            return node;
        } else {
            throw new RuntimeException("Unknown type value " + av);
        }
    }

    /**
     * Parses the supplied list of {@link AttributeValue} into an
     * {@link ArrayNode}.
     *
     * @param item
     *            A {@link List} of {@link AttributeValue} unloaded from
     *            supplied {@link ScanResult}
     * @param depth
     *            The depth from the top level structure
     * @return A {@link JsonNode} that is parsed from the supplied list of
     *         {@link AttributeValue}.
     * @throws UnsupportedEncodingException
     *             If the encoding from ByteArray from the
     *             {@link AttributeValue#getB()} is not encoded with "UTF-8".
     */
    private JsonNode listToJsonArray(final List<AttributeValue> item, final int depth)
            throws UnsupportedEncodingException {
        if (item != null) {
            final ArrayNode node = JsonNodeFactory.instance.arrayNode();
            for (final AttributeValue value : item) {
                node.add(getJsonNode(value, depth + 1));
            }
            return node;
        }
        throw new IllegalArgumentException("Item cannot be null");
    }
}
