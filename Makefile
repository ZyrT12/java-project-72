build:
	make -C app ./gradlew clean build

install:
	make -C app ./gradlew clean install

run:
	make -C app ./gradlew run

lint:
	make -C app ./gradlew checkstyleMain

report:
	make -C app ./gradlew jacocoTestReport

run-dist:
	./app/build/install/app/bin/app

build-run: build run

.PHONY: build install run lint report run-dist build-run
