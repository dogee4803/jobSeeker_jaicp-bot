require: functions.js
require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Здраствуйте, моя задача помочь вам с выбором вакансии. 

    state: Hello
        intent!: /привет
        a: Привет привет

    state: Bye
        intent!: /пока
        a: До свидания! Надеюсь смог вам помочь.

    state: Clarification
        event!: noJobFound
        a: К сожалению, я не нашел вакансий по вашему запросу. Попробуйте изменить запрос или указать другие параметры.
    
    state: HandleApiResponse
        intent!: /поиск
        script:
            # Выполняем запрос через API для поиска вакансий
            fetchVacancies().then(function (res) {
                if (res.status === '200' && res.results.vacancies.length > 0) {
                    $session.vacancies = res.results.vacancies;
                    $session.page = 0;
                    $reactions.answer("Вакансии успешно найдены! Перехожу к отображению...");
                    showPage($session.page, $session.vacancies);
                } else {
                    $reactions.answer("К сожалению, вакансии по вашему запросу не найдены.");
                    $session.nextState = 'Clarification';
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
            });
    
    state: NextPage
        intent!: /след
        script:
            if ($session.page !== undefined && $session.vacancies) {
                $session.page++;
                if ($session.page * 3 < $session.vacancies.length) {
                    showPage($session.page, $session.vacancies);
                } else {
                    $reactions.answer("Это последняя страница.");
                }
            } else {
                $reactions.answer("Сначала выполните поиск вакансий.");
            }
            
    state: PrevPage
        intent!: /пред
        script:
            if ($session.page > 0) {
                $session.page--;
                showPage($session.page, $session.vacancies);
            } else {
                $reactions.answer("Это первая страница.");
            }

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}
        
    state: Profanity
        q!: * @mystem.obsc *
        a: Пожалуйста, избегайте нецензурных выражений. Мы стараемся поддерживать уважительную атмосферу.

    state: ProfanityAlt
        q!: * @mlps-obscene.obscene *
        a: Ваше сообщение содержит нецензурные слова. Пожалуйста, используйте более корректные выражения.