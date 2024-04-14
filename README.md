1. Скачать и установить JDK версии 14 или выше, если ещё не установлена. Ссылка на скачивание: https://jdk.java.net/java-se-ri/14
2. Прописать переменную окружения JAVA_HOME, которая должна указывать на директорию, в которую установлена Java. JAVA_HOME=путь к директории\jdk-14
3. Добавить в переменную окружения Path путь %JAVA_HOME%\bin
4. Скачать и установить Maven (Binary zip archive). Ссылка для скачивания: https://maven.apache.org/download.cgi
5. Распаковать архив с Maven в любую директорию
6. Установить переменную окружения M2_HOME вида "путь к директории\apache-maven-3.9.6"
7. Добавить в переменную окружения Path путь "%M2_HOME%\bin"
8. Через git bash зайти в папку, куда нужно скачать проект, и выполнить команду "git clone git@github.com:d-nihil/AvitoTest.git"
9. Зайти в git bash в папку AvitoTest и выполнить команду "mvn test"