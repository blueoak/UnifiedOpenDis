#!/bin/sh
cd "${0%/*}"
cd ../
jar cf jars/entities_oth.jar -C target/classes edu/nps/moves/dis/entities/oth
jar cf jars/entities_oth_src.jar -C generated/dis7/sisoEntities/java edu/nps/moves/dis/entities/oth
