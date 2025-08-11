# Makefile
run-dist:
	./build/install/app/bin/app

clean:
	./gradlew clean

build:
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
