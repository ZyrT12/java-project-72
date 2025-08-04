# Makefile
run-dist:
	./app/build/install/app/bin/app

setup:
    make -C app setup

build:
	make -C app clean build

run:
	.make -C app run

report:
	make -C app report

clean:
	make -C app clean

install:
	make -C app install

run:
	make -C app run

report:
	.make -C app jacocoTestReport

lint:
	make -C app checkstyleMain
