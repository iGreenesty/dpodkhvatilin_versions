//SCRIPTSD4000426
//Код: Создание голосований
//Назначение:
/**
 * Создание голосований в согласовании при входе в состояние "В
 процессе"
 */
//Категория: создание вложенных объектов
//ПАРАМЕТРЫ-----------------------------------------------
def VOTE_FQN = 'vote$vote'; // Код метакласса "Голосование"
def VOTERS_EMPL = 'cabEmployee'; // Код атрибута "Согласующий комитет - сотрудники" в классе "Согласование"
def VOTERS_OU = 'cabOU'; // Код атрибута "Согласующий комитет - отделы" в классе "Согласование"
def VOTERS_TEAM = 'cabTeam'; // Код атрибута "Согласующий комитет - команды" в классе "Согласование"
def VOTER = 'voter' // Код атрибута "Голосующий", типа "агрегирующий атрибут" в классе "Голосование"
//ОСНОВНОЙ БЛОК--------------------------------------------
//Проверка правильности указанных параметров
def error = '';
if (!api.metainfo.metaClassExists(VOTE_FQN)) utils.throwReadableException('Указанный в параметрах метакласс не существует');

error = api.metainfo.checkAttributeExisting(subject, VOTERS_EMPL);
if (error != '') utils.throwReadableExceptionutils.throwReadableException(error);
    
error = api.metainfo.checkAttributeExisting(subject, VOTERS_OU);
if (error != '') utils.throwReadableException(error);

error = api.metainfo.checkAttributeExisting(subject, VOTERS_TEAM);
if (error != '') utils.throwReadableException(error);

error = api.metainfo.checkAttributeExisting(VOTE_FQN, VOTER);
if (error != '') utils.throwReadableException(error);

def possibleTypes = ['aggregate'];
error = api.metainfo.checkAttributeType(VOTE_FQN, VOTER, possibleTypes);
if (error != '') utils.throwReadableException(error);

if ((subject[VOTERS_EMPL].isEmpty()) && (subject[VOTERS_OU].isEmpty()) && (subject[VOTERS_TEAM].isEmpty()))
    utils.throwReadableException('Вы должны задать согласующий коммитет');
    
def attrs = [:];
attrs['parent'] = subject;
// Создание голосования для сотрудников
if (subject[VOTERS_EMPL] != null)
{
    subject[VOTERS_EMPL].each({
    	voter -> attrs[VOTER + '_em'] = voter; attrs[VOTER + '_ou'] = voter.parent;
    	utils.create(VOTE_FQN, attrs);
    	attrs[VOTER + '_em'] = null;
    });
}

// Создание голосования для отделов
if (subject[VOTERS_OU] != null)
{
  	subject[VOTERS_OU].each({
      	voter -> attrs[VOTER + '_ou'] = voter;
    	utils.create(VOTE_FQN, attrs);
    	attrs[VOTER + '_ou'] = null;
    });
}

// Создание голосования для команд
if (subject[VOTERS_TEAM] != null)
{
  	subject[VOTERS_TEAM].each({
      	voter -> attrs[VOTER + '_te'] = voter;
    	utils.create(VOTE_FQN, attrs);
    	attrs[VOTER + '_te'] = null;
    });
}