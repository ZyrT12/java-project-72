run-dist:
	make -C app run-dist

clean:
	make -C app clean

build:
	make -C app build

install:
	make -C app install

run:
	make -C app run

report:
	make -C app report

lint:
	make -C app lint

build-run: build run

.PHONY: run-dist clean build install run report lint build-run
