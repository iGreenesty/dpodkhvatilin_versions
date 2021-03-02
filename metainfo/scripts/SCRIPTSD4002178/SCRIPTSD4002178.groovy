/*! UTF8 */
//Автор: eboronina
//Дата создания: 20.12.2019
//Код: SCRIPTSD4002178
//Назначение:
/**
 * Создание статьи базы знаний
 * @param COPY_FROM_PARAMS - список кодов параметров,
 * которые будут копироваться в атрибуты
 * @param METACLASS_BY_FLAG - определение метакласса в
 * зависимости от значения флажка "Внутренняя"
 * @param FLAG_CODE - код параметра флажка "Внутренняя"
 * @param STATIC_ATTRS - статичные атрибуты
 * Сценарий определяет тип создаваемого объекта в зависимости
 * от флажка. Затем копирует атрибуты из параметров и создает
 * статью базы знаний. В результате выдает сообщение с ссылкой
 * на созданную статью
 */
//Версия: 4.10.0+
//Категория: 
//Параметры------------------------------------------------------
def COPY_FROM_PARAMS = ['title', 'parent', 'text', 'services']
// 0 - внешняя, 1 - внутренняя
def METACLASS_BY_FLAG = ['KB$KBArticleOpen', 'KB$KBArticle']
def FLAG_CODE = 'isInternal'
def STATIC_ATTRS = [
  'author'      : user,
  'basedOnReq'  : subject
]
//Функции--------------------------------------------------------

//Основной блок -------------------------------------------------
def mc = METACLASS_BY_FLAG[(params[(FLAG_CODE)]) ? 1 : 0]
def attrs = STATIC_ATTRS
COPY_FROM_PARAMS.each {
  attrs.put(it, params[(it)])
}

def article = utils.create(mc, attrs)
def msg = "Создана статья ${api.web.asLink(api.web.open(article), article.title)}"
result.showMessage('Успех', msg)