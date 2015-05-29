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
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Goal which uploads files to oneskyapp.com
 */
@Mojo(name = "upload-files", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class UploadFileOneSkyAppMojo extends AbstractOneSkyAppMojo {

    @Parameter(property = "outputDir", required = true)
    private List<File> files;

    @Parameter(property = "locale")
    private String locale;

    @Parameter(property = "fileFormat")
    private String fileFormat;
    
    @Override
    public void execute() throws MojoExecutionException {
        super.execute();
        if (files == null || files.isEmpty() || locale == null || fileFormat == null) {
            showHelp();
        }
        uploadFiles();
    }

    private void uploadFiles() throws MojoExecutionException {
        try {
            for (File file : files) {
                System.out.println(String.format("Uploading %1$s", file.getName()));
                OkHttpClient okHttpClient = new OkHttpClient();
                final String url = String.format(API_ENDPOINT + "projects/%1$s/files?file_format=%2$s&locale=%3$s&%4$s", projectId, fileFormat, locale, getAuthParams());

                RequestBody requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addPart(
                                Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\""),
                                RequestBody.create(MediaType.parse("text/plain"), file))
                        .build();

                final Request request = new Request.Builder().post(requestBody).url(url).build();
                final Response response = okHttpClient.newCall(request).execute();

                if (response.code() == 201) {
                    System.out.println(String.format("Successfully uploaded %1$s", file.getName()));
                } else {
                    throw new MojoExecutionException(String.format("OneSkyApp API returned %1$s: %2s, %3$s", response.code(), response.message(), response.body().string()));
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }
}
