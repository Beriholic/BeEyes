.PHONY: clean
clean:
	rm -rf ./internel/lib/sysinfo/target
.PHONY: build-all
build-all:
	cd internel/lib/sysinfo && cargo build --release
	go build
.PHONY: build-sysinfo
build-sysinfo:
	cd internel/lib/sysinfo && cargo build --release
.PHONY: build
build:
	go build