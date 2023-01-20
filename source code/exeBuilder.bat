START /W gradlew desktop:dist
ECHO Back from gradlew desktop:dist

@RD /S /Q "C:\Users\Sandra Moen\dev\Drawing game\source code\packr build files"
CD C:\Users\Sandra Moen\dev

java -jar packr-all-4.0.0.jar ^
--platform "windows64" ^
--jdk "C:\Program Files\Java\jdk-15.0.1" ^
--useZgcIfSupportedOs ^
--executable "desktop-1.0" ^
--classpath "C:\Users\Sandra Moen\dev\Drawing game\source code\desktop\build\libs\desktop-1.0.jar" ^
--mainclass "no.sandramoen.drawingGame.DesktopLauncher" ^
--resources "C:\Users\Sandra Moen\dev\Drawing game\source code\assets" ^
--output "C:\Users\Sandra Moen\dev\Drawing game\source code\packr build files"

CD C:\Users\Sandra Moen\dev\Drawing game\source code
