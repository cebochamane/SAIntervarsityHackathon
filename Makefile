# ===== Makefile for Java blockchain (Windows-friendly) =====
# Usage:
#   make deps           # ensure lib folder + bcprov.jar exists (see note in target)
#   make compile        # compile to out/
#   make run            # run main class (default: NoobChain)
#   make run RUN_CLASS=Demo
#   make demo           # run and save output to demo/run-output.txt
#   make clean          # remove out/

JAVAC      := javac
JAVA       := java
SRC_DIR    := src\blockchain
OUT_DIR    := out
LIB_DIR    := lib
CP         := $(OUT_DIR);$(LIB_DIR)\*

# Change this if your entry class is different
RUN_CLASS  ?= NoobChain

# All .java sources
SOURCES    := $(wildcard $(SRC_DIR)\*.java)

.PHONY: all deps compile run demo clean

all: deps compile

deps:
@if not exist $(LIB_DIR) mkdir $(LIB_DIR)
@if not exist $(LIB_DIR)\bcprov.jar ( \
echo [INFO] Put BouncyCastle provider jar at ^"$(LIB_DIR)\bcprov.jar^" ^(see instructions below^) & \
echo [INFO] Or run the PowerShell command provided in the chat to download it automatically. \
)

compile:
@if not exist $(OUT_DIR) mkdir $(OUT_DIR)
@echo [BUILD] Compiling sources...
@$(JAVAC) -cp "$(CP)" -d $(OUT_DIR) $(SOURCES)
@echo [OK] Classes in $(OUT_DIR)

run:
@echo [RUN] $(RUN_CLASS)
@$(JAVA) -cp "$(CP)" $(RUN_CLASS)

demo:
@if not exist demo mkdir demo
@echo [DEMO] Saving output to demo\run-output.txt
@$(JAVA) -cp "$(CP)" $(RUN_CLASS) > demo\run-output.txt
@echo [OK] demo\run-output.txt written

clean:
@if exist $(OUT_DIR) rmdir /S /Q $(OUT_DIR)
@echo [CLEAN] Removed $(OUT_DIR)
