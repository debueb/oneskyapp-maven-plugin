# oneskyapp-maven-plugin

This plugin allows you to download and upload translation files from the excellent online translation service [OneSkyApp](http://www.oneskyapp.com)

### How to use

Add the following to your pom.xml `<plugins>` section, replacing VARIABLES 

```
<plugin>
  <groupId>de.ebf</groupId>
  <artifactId>oneskyapp-maven-plugin</artifactId>
  <version>CURRENT_VERSION</version>
  <configuration>
      <publicKey>PUBLIC KEY</publicKey>             <!-- http://support.oneskyapp.com/support/solutions/articles/89104-how-to-find-your-api -->
      <secretKey>SECRET KEY</secretKey>             <!-- http://support.oneskyapp.com/support/solutions/articles/89104-how-to-find-your-api -->
      <projectId>PROJECT ID</projectId>             <!-- browse your project and take the ID from the URL -->
      <failOnError>false</failOnError>	            <!-- defaults to true -->
  </configuration>
  <executions>
      <execution>
          <id>get-translations</id>
          <phase>process-resources</phase>
          <goals>
              <goal>get-translations</goal>           <!-- get-translations downloads translation files -->
          </goals>
          <configuration>
              <sourceFileNames>                       <!-- the file name of the original upload -->
                  <param>messages.properties</param>  
                  <param>ValidationMessagees.properties</param>  
              </sourceFileNames>
              <locales>                               <!-- the translations to download. files will be stored as [outputDir]/[sourceFileName]_[locale].[sourceFileNameExtension] -->
                  <param>de</param>
                  <param>fr</param>
              </locales>
              <sourceLocale>en</sourceLocale>         <!-- optional: will be stored as [outputDir]/[sourceFileName].[sourceFileNameExtension] -->
              <outputDir>src/main/webapp/WEB-INF/classes/</outputDir> <!-- relative or absolute file paths -->
          </configuration>
      </execution>
      <execution>
          <id>upload-files</id>
          <phase>process-resources</phase>
          <goals>
              <goal>upload-files</goal>             <!-- upload one or more files that need translation -->
          </goals>
          <configuration>
              <files>                               <!-- relative or absolute file paths -->
                  <param>src/main/webapp/WEB-INF/classes/messages.properties</param> 
              </files>
              <locale>en-US</locale>                <!-- the locale of the original file that needs translation -->
              <fileFormat>JAVA_PROPERTIES</fileFormat> <!-- https://github.com/onesky/api-documentation-platform/blob/master/reference/format.md -->
          </configuration>
      </execution>
  </executions>
</plugin>
```

### Run

```mvn clean process-resources```
