package de.ebf;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * Goal which downloads translations from oneskyapp.com
 */
@Mojo(name = "get-translations", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GetTranslationsOneSkyAppMojo extends AbstractOneSkyAppMojo {

    @Parameter(property = "sourceFileName")
    private List<String> sourceFileNames;

    @Parameter(property = "locales")
    private List<String> locales;

    @Parameter(property = "outputDir", required = true)
    private File outputDir;

    /**
     * download translation files from oneskyapp
     * 
     * @throws MojoExecutionException in case of missing parameters or runtime exceptions
     */
    @Override
    public void execute() throws MojoExecutionException {
        super.execute();
        if (sourceFileNames == null || sourceFileNames.isEmpty() || locales == null || locales.isEmpty()) {
            showHelp();
        }
        getTranslations();
    }

    private void getTranslations() throws MojoExecutionException {
        try {
            for (String sourceFileName : sourceFileNames) {
                for (String locale : locales) {
                    System.out.println(String.format("Downloading %1s translations for %2s", locale, sourceFileName));
                    
                    OkHttpClient okHttpClient = new OkHttpClient();
                    final String url = String.format(API_ENDPOINT + "projects/%1$s/translations?locale=%2$s&source_file_name=%3$s&%4$s", projectId, locale, sourceFileName, getAuthParams());
                    final Request request = new Request.Builder().get().url(url).build();
                    final Response response = okHttpClient.newCall(request).execute();
                    
                    if (response.code() == 200) {
                        if (!outputDir.exists()) {
                            outputDir.mkdirs();
                        }
                        //even though the OneSkyApp API documentation states that the file name should be sourceFileName_locale.sourceFileNameExtension, it is just locale.sourceFileNameExtension)
                        //https://github.com/onesky/api-documentation-platform/blob/master/resources/translation.md

                        String targetFileName = sourceFileName + "_" + locale;
                        int index = sourceFileName.lastIndexOf(".");
                        if (index>0){
                            targetFileName = sourceFileName.substring(0, index) + "_" + locale + "." + sourceFileName.substring(index+1);
                        }
                        File outputFile = new File(outputDir, targetFileName);
                        outputFile.createNewFile();
                        final InputStream inputStream = response.body().byteStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                        IOUtils.copy(inputStream, fileOutputStream);
                        System.out.println(String.format("Successfully downloaded %1s translation for %2s to %3s", locale, sourceFileName, outputFile.getName()));
                    } else {
                        throw new MojoExecutionException(String.format("OneSkyApp API returned %1$s: %2s", response.code(), response.message()));
                    }
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }
}
