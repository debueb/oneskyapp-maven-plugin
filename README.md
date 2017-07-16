# oneskyapp-maven-plugin

This plugin allows you to download and upload translation files from the excellent online translation service [OneSkyApp](http://www.oneskyapp.com)

### How to use

Add the following to your pom.xml `<plugins>` section, replacing the following VARIABLES

variable | value
--- | ---
CURRENT_VERSION | [latest version published on maven central](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22oneskyapp-maven-plugin%22)
PUBLIC_KEY | [your OneSkyApp public key](http://support.oneskyapp.com/support/solutions/articles/89104-how-to-find-your-api)
SECRET_KEY | [your OneSkyApp secret key](http://support.oneskyapp.com/support/solutions/articles/89104-how-to-find-your-api)
PROJECT_ID | browse your project and take the ID from the URL

### Example pom.xml config
```xml
<plugin>
  <groupId>de.ebf</groupId>
  <artifactId>oneskyapp-maven-plugin</artifactId>
  <version>[CURRENT_VERSION]</version>
  <configuration>
      <publicKey>[PUBLIC_KEY]</publicKey>
      <secretKey>[SECRET_KEY]</secretKey>
      <projectId>[PROJECT_ID]</projectId>
      <failOnError>false</failOnError>
  </configuration>
  <executions>
      <execution>
          <id>get-translations</id>
          <phase>process-resources</phase>
          <goals>
              <goal>get-translations</goal>
          </goals>
          <configuration>
              <sourceFileNames>
                  <param>messages.properties</param>  
                  <param>ValidationMessagees.properties</param>  
              </sourceFileNames>
              <locales>
                  <param>de</param>
                  <param>fr</param>
              </locales>
              <sourceLocale>en</sourceLocale>
              <outputDir>src/main/webapp/WEB-INF/classes/</outputDir>
          </configuration>
      </execution>
      <execution>
          <id>upload-files</id>
          <phase>process-resources</phase>
          <goals>
              <goal>upload-files</goal>
          </goals>
          <configuration>
              <files>
                  <param>src/main/webapp/WEB-INF/classes/messages.properties</param> 
              </files>
              <locale>en-US</locale>
              <fileFormat>JAVA_PROPERTIES</fileFormat>
          </configuration>
      </execution>
  </executions>
</plugin>
```

### Goals and configurations params

goal | description | configuration params
--- | --- | ---
get-translations | download translation files | - `<sourceFileNames>` the name of the files to download <br/> - `<locales>` the translations to download. files will be stored as `[outputDir]/[sourceFileName]_[locale].[sourceFileNameExtension]`<br/> - `<sourceLocale>` optional: will be stored as `[outputDir]/[sourceFileName].[sourceFileNameExtension]` <br /> - `<outputDir>` relative or absolute path for the download files
upload-files | upload translation files. | - `<files>` files to upload <br/> - `<locale>` the locale of the upload <br/> `<fileFormat>` - [the file format](https://github.com/onesky/api-documentation-platform/blob/master/reference/format.md)

### Execute maven plugin

By default, the plugin runs during the `process-resources` phase

```mvn clean process-resources```
