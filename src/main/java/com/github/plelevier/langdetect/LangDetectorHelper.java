package com.github.plelevier.langdetect;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.cybozu.labs.langdetect.*;
import org.apache.commons.io.IOUtils;

public class LangDetectorHelper
{
    public static void loadProfileFromResources(String resourceDir) throws LangDetectException, IOException, URISyntaxException
    {
        CodeSource src = DetectorFactory.class.getProtectionDomain().getCodeSource();

        if (src != null)
        {
            URL jar = src.getLocation();
            InputStream in = null;
            List<String> json_profiles = new ArrayList<String>();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry ze;

            while ((ze = zip.getNextEntry()) != null)
            {
                String entryName = ze.getName();
                if (entryName.startsWith(resourceDir))
                {
                    try
                    {
                        in = DetectorFactory.class.getResourceAsStream("/" + entryName);
                        if (in != null)
                        {
                            json_profiles.add(IOUtils.toString(in, "UTF-8"));
                        }
                    }
                    finally
                    {
                        try
                        {
                            if (in !=null) in.close();
                        } catch (IOException e) {}
                    }
                }
            }
            DetectorFactory.loadProfile(json_profiles);
        }
    }
}
