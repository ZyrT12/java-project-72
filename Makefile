build:
	cd app && chmod +x gradlew && ./gradlew clean build

install:
	cd app && ./gradlew clean install

run:
	cd app && ./gradlew run

lint:
	cd app && ./gradlew checkstyleMain

report:
	cd app && ./gradlew jacocoTestReport

run-dist:
	./app/build/install/app/bin/app

setup:
	chmod +x app/gradlew
	cd app && ./gradlew wrapper --gradle-version 8.14

build-run: build run

.PHONY: build install run lint report run-dist build-run
