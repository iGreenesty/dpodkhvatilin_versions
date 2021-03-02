/*! UTF-8 */

/**
 * Дата: 16.09.2015
 * Назначение: модуль для конфигурации ITSM365
 */

/**
 * Возвращает часовой пояс из настроек компании
 */
def getCompanyTimeZone()
{
	def TIME_ZONE_ATTR_CODE = 'timeZone'
	def ROOT_CLASS_CODE = 'root'

	def rootMetaClass = api.metainfo.getMetaClass(ROOT_CLASS_CODE)	
	def root = utils.get(ROOT_CLASS_CODE, [:])

	if (rootMetaClass.hasAttribute(TIME_ZONE_ATTR_CODE))
	{
		return root[TIME_ZONE_ATTR_CODE]?.code
	}
	else
	{
		return null
	}	
}

/**
 * Возвращает часовой пояс пользователя из персональных настроек
 */
def getUserTimeZone(user)
{
	if (user)
	{
		def settings = api.employee.getPersonalSettings(user.UUID)

		return settings.getTimeZone()
	}
	else
	{
		return null
	}
}

/**
 * Возвращает часовой пояс сервера
 */
def getDefaultTimeZone()
{
	return TimeZone.getDefault().getID()
}

/**
 * Возвращает дату/время, отформатированную в указанном часовом поясе
 */
def getDateTimeInTimeZone(dateTime, timeZone)
{
    Calendar t = new GregorianCalendar(TimeZone.getTimeZone(timeZone));
    t.setTimeInMillis(dateTime.getTime())

    return sprintf("%02d.%02d.%04d %02d:%02d", t.get(Calendar.DATE), t.get(Calendar.MONTH) + 1, t.get(Calendar.YEAR), t.get(Calendar.HOUR_OF_DAY), t.get(Calendar.MINUTE))
}

/**
 * Возвращает значение дата/время, отформатированное в часовом поясе пользователя
 * Если часовой пояс пользователя не указан, то используется часовой пояс из настроек компании
 * Если часовой пояс компании не указан, то используется часовой пояс сервера
 */
def getDateTimeInUserTimeZone(dateTime, user)
{
	def timeZone = getUserTimeZone(user) ?: getCompanyTimeZone() ?: getDefaultTimeZone()

	return getDateTimeInTimeZone(dateTime, timeZone)
}