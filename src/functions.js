function fetchVacancies() {
    return $http.get("http://opendata.trudvsem.ru/api/v1/vacancies/region/03?text=программист", {
        timeout: 10000
    });
}

// Функция для вывода вакансий с пагинацией
function showPage(page, vacancies) {
    var pageSize = 3;

    // Вычисляем индекс начала и конца текущей страницы
    var startIndex = page * pageSize;
    var endIndex = Math.min(startIndex + pageSize, vacancies.length);

    var message = "Вакансии с " + (startIndex + 1) + " по " + endIndex + " из " + vacancies.length + ":\n\n";

    // Формируем сообщение с вакансиями для текущей страницы
    vacancies.slice(startIndex, endIndex).forEach(function (vacancy) {
        var contact = (vacancy.vacancy.contact_list && vacancy.vacancy.contact_list.length > 0)
            ? vacancy.vacancy.contact_list[0].contact_value
            : "нет данных";

        message += "- **" + vacancy.vacancy.company.name + "** в *" + vacancy.vacancy.region.name + "*\n" +
                   "  Зарплата: " + (vacancy.vacancy.salary || "не указана") + "\n" +
                   "  Контакты: " + contact + "\n" +
                   "  [Подробнее](" + vacancy.vacancy.vac_url + ")\n\n";
    });

    $reactions.answer(message);

    var buttons = [];
    if (startIndex > 0) {
        buttons.push("предыдущая страница");
    }
    if (endIndex < vacancies.length) {
        buttons.push("следующая страница");
    }

    // Если есть кнопки, отображаем их
    if (buttons.length > 0) {
        $reactions.buttons(buttons);
    }
}


// Определяем вопросы для анкеты
var surveyQuestions = [
    { key: "jobTitle", question: "Какую профессию вы ищете? Например: программист, инженер." },
    { key: "region", question: "В каком регионе вы ищете работу? Например: Республика Бурятия, Московская область." },
    { key: "city", question: "В каком городе вы ищете работу? Например: Улан-Удэ, Москва." },
    { key: "employment", question: "Какой тип занятости вас интересует? Например: полная занятость, временная, частичная." },
    { key: "schedule", question: "Какой график вам подходит? Например: Полный рабочий день, Сменная работа." },
    { key: "salary", question: "Какую минимальную зарплату вы ожидаете? Укажите сумму в рублях." },
];


// Функция для получения следующего вопроса
function askQuestion(survey) {
    for (var i = 0; i < surveyQuestions.length; i++) {
        var question = surveyQuestions[i];
        if (!survey[question.key]) {
            return question;
        }
    }
    return null; // Все вопросы заданы
}

function fillSurvey(survey) {
    if (!survey.jobTitle) {
        $reactions.answer("Какую профессию вы ищете? Например: программист, инженер.");
        $reactions.transition("AwaitJobTitle");
    }
}