.PHONY: clean
clean:
	rm -rf ./internal/lib/monitor/target
.PHONY: build-all
build-all:
	cd internal/lib/monitor && cargo build --release
	go build
.PHONY: build-monitor
build-monitor:
	cd internal/lib/monitor && cargo build --release
.PHONY: build
build:
	go build