//Автор: dshirykalov
//Дата создания: 23.04.2015
//Назначение:
/**
 * назначение скрипта: устанавливает флаг добавления неприватного комментария.
 */

def COMMENT_ADDED_ATTR_CODE = 'commentAdded'
def PRIVATE_ATTR_CODE = 'private'
def AUTHOR_ATTR_CODE = 'author'
def CLIENT_ATTR_CODE = 'clientEmployee'

def author = sourceObject[AUTHOR_ATTR_CODE]
def clientEm = subject[CLIENT_ATTR_CODE]

// если (комментарий неприватный) и (комментарий добавил не контрагент-сотрудник)
if (!sourceObject[PRIVATE_ATTR_CODE] && (!author || !clientEm || author.UUID != clientEm.UUID)) {
  utils.edit(subject, [(COMMENT_ADDED_ATTR_CODE) : true])
}