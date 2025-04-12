javac -d build $(find src -name "*.java") && jar cf codebase.jar -C build .
