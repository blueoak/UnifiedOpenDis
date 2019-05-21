#!/bin/sh
cd "${0%/*}"
cd ../
jar cf jars/entities_deu.jar -C target/classes edu/nps/moves/dis/entities/deu
jar cf jars/entities_deu_src.jar -C generated/dis7/sisoEntities/java edu/nps/moves/dis/entities/deu
