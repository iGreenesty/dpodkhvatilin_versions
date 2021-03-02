/*! UTF8 */
//Автор: vsapozhnikova
//Дата создания: 14.07.2020
//Код: 
//Назначение:
/**
 * Модуль
 */
//Версия: 4.8.*
//Категория:

package ru.itsm365.notificationAccessHelper 

//Параметры------------------------------------------------------
class Constants {
  	//атрибут в настройках компании "Выдавать ключи доступа в оповещениях"
    static String INSERT_KEY_ATTR = 'keyInsert'
  	//атрибут в настройках компании "Выдавать ключи доступа сотрудникам без логина"
    static String WITHOUT_LOGIN_ATTR = 'keyNotLogin'
  	//атрибут в настройках компании "Многоразовые ключи доступа"
    static String REUSABLE_KEY_ATTR = 'keyReusable'
  	//атрибут в настройках компании "Срок жизни ключа доступа (в днях)"
    static String LIFES_DAYS_ATTR = 'keyLifesDays'
}
//Функции--------------------------------------------------------
def getRoot() {
    return utils.get('root', [:])
}


boolean getInsertKey() {
    return root[Constants.INSERT_KEY_ATTR]
}

boolean getWithoutLogin() {
    return root[Constants.WITHOUT_LOGIN_ATTR]
}

boolean getReusableKey() {
    return root[Constants.REUSABLE_KEY_ATTR]
}

Integer getLifesDays() {
    return root[Constants.LIFES_DAYS_ATTR]
}


//Метод возращает ключ доступа по логину
def getAccessKey(def login) {
    def accessKey = api.auth.getAccessKey(login)
    return prepareAccessKey(accessKey)
}

//Метод возращает ключ доступа по UUID сотрудника
def getAccessKeyByUUID(def UUID) {
    def accessKey = api.auth.getAccessKeyByUUID(UUID)
    return prepareAccessKey(accessKey)
}


//Метод выставляет свойства ключу согласно настройкам 
def prepareAccessKey(def accessKey) {
	//Если ключ многоразовый 
    if (reusableKey) {
		//то выставляем свойство "многоразовый"
        accessKey.setReusable()
    } else {
      	//то выставляем свойство "одноразовый"
        accessKey.setDisposable()
    }
  
  	//Выставляем срок жизни ключа
    if (lifesDays) {
        accessKey.setDeadlineDays(lifesDays)
    }
    return accessKey
}

String open(def user, def object) {
	// если нет userа, то возвращаем ссылку без ключа
  	if(!user){
      //return api.web.open(object)
      // если нет userа, то возвращаем null, чтобы и ссылки не было
      return null 
    }
    def accessKey
  	
  	// если в настройках указано, что используем ключ,  то формируем ключ
    if (insertKey) {
		//если есть логин, то формируем по логину
		// иначе по UUID userа
        accessKey = user.login ? getAccessKey(user.login) : getAccessKeyByUUID(user.UUID)
    }
  	//если в настройках указано, что не формируем ключ для userов без логина, и у usera нет логина
	// то возвращаем null
    if (!withoutLogin && !user.login) {
        return null
    }
  	
  	//возвращаем ссылку с ключом 
    return api.web.open(object, accessKey)
}

/**
 * user - пользователь
 * object - объект, на каторый указвает ссылка 
 * text - текст ссылки 
 * alwaysPrint -нужно ли отдавать текст, если ссылки нет (да)-нужно, (нет)- ненужно
 */
String newHyperlink(def user, def object, String text, boolean alwaysPrint = true) {
	//формируем ссылку
    String url = open(user, object)
	//если ссылка есть, то отдаем ее
	//если ссылки нет, то проверяем нужно ли отдавать просто текст 
	//если нужно, отдаем текст, иначе отдаем пустую строку
    return url ? api.types.newHyperlink(text, url) : (alwaysPrint ? text : '')
}