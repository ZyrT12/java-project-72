run-dist:
	./build/install/app/bin/app

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean installDist

run:
	./gradlew run

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain

build-run: build run

.PHONY: run-dist clean build install run report lint build-run
