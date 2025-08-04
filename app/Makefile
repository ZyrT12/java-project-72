# Makefile
run-dist:
	./build/install/app/bin/app

setup:
	chmod +x app/gradlew
	./gradlew wrapper --gradle-version 8.14

clean:
	./gradlew clean

build:
    chmod +x gradlew
	./gradlew clean build

install:
	./gradlew clean install

run:
	./gradlew run

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain

build-run: build run

.PHONY: build
