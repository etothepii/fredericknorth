package uk.co.epii.conservatives.fredericknorth.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

/**
 * User: James Robinson
 * Date: 22/06/2013
 * Time: 01:25
 */
public class BufferedResourceReader implements Iterable<String>, Iterator<String> {

    private final URL resource;

    public BufferedResourceReader(URL resource) {
        this.resource = resource;
    }

    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private String current;
    private String next;
    private boolean eof = false;


    @Override
    public Iterator<String> iterator() {
        openStream();
        inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        eof = false;
        return this;
    }

    private void openStream() {
        try {
            inputStream = resource.openStream();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        if (next == null && !eof) {
            readNext();
            if (next == null) {
                closeOut();
                return false;
            }
        }
        return !eof;
    }

    private void readNext() {
        try {
            next = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeOut() {
        eof = true;
        try {
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String next() {
        current = next;
        next = null;
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("The BufferedResourceReader is a read only iterator");
    }
}
