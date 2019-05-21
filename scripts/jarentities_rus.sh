#!/bin/sh
cd "${0%/*}"
cd ../
jar cf jars/entities_rus.jar -C target/classes edu/nps/moves/dis/entities/rus
jar cf jars/entities_rus_src.jar -C generated/dis7/sisoEntities/java edu/nps/moves/dis/entities/rus