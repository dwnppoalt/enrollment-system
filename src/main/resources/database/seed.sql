USE enrollment_system;

INSERT INTO programs (
    id,
    code,
    name,
    description,
    duration_years,
    department,
    college
) VALUES

-- =========================================================
-- COLLEGE OF AGRICULTURE
-- =========================================================

(
    UUID(),
    'BSA',
    'Bachelor of Science in Agriculture',
    'A four-year program covering scientific, technical, entrepreneurial, and professional aspects of agriculture, with specializations including Animal Science, Crop Science, and Crop Protection.',
    4,
    'Department of Agriculture',
    'College of Agriculture'
),

(
    UUID(),
    'BSAB',
    'Bachelor of Science in Agribusiness',
    'A four-year program preparing students for careers in agribusiness management, entrepreneurship, research, extension, and related agricultural business fields.',
    4,
    'Department of Agribusiness',
    'College of Agriculture'
),


-- =========================================================
-- COLLEGE OF ARTS AND SOCIAL SCIENCES
-- =========================================================

(
    UUID(),
    'BAFIL',
    'Bachelor of Arts in Filipino',
    'A program focused on Filipino language, communication, literature, creative writing, research, and translation.',
    4,
    'Department of Filipino',
    'College of Arts and Social Sciences'
),

(
    UUID(),
    'BALIT',
    'Bachelor of Arts in Literature',
    'A program specializing in literary and cultural studies, including literature, media studies, cultural studies, and related disciplines.',
    4,
    'Department of English and Humanities',
    'College of Arts and Social Sciences'
),

(
    UUID(),
    'BASS',
    'Bachelor of Arts in Social Sciences',
    'A multidisciplinary program covering social science theories, research, policy, and community and national development.',
    4,
    'Department of Social Sciences',
    'College of Arts and Social Sciences'
),

(
    UUID(),
    'BSDC',
    'Bachelor of Science in Development Communication',
    'A program focused on communication theories, development communication practice, research, and communication for community and societal development.',
    4,
    'Department of Communication and Development Studies',
    'College of Arts and Social Sciences'
),

(
    UUID(),
    'BSPsych',
    'Bachelor of Science in Psychology',
    'A program covering the scientific study of human behavior and mental processes, psychological assessment, research, and applied psychology.',
    4,
    'Department of Psychology',
    'College of Arts and Social Sciences'
),

(
    UUID(),
    'BAIS',
    'Bachelor of Arts in International Studies',
    'A program focused on international relations, global affairs, sustainable development, and related international studies.',
    4,
    'Department of Social Sciences',
    'College of Arts and Social Sciences'
),


-- =========================================================
-- COLLEGE OF BUSINESS AND ACCOUNTANCY
-- =========================================================

(
    UUID(),
    'BSAC',
    'Bachelor of Science in Accountancy',
    'A professional accounting program preparing students for careers in accounting, financial reporting, auditing, taxation, and related fields.',
    4,
    'Department of Accountancy',
    'College of Business and Accountancy'
),

(
    UUID(),
    'BSBA',
    'Bachelor of Science in Business Administration',
    'A business program covering business administration and related fields, with areas including Business Economics, Human Resource Management, and Marketing Management.',
    4,
    'Department of Business Administration',
    'College of Business and Accountancy'
),

(
    UUID(),
    'BSENTREP',
    'Bachelor of Science in Entrepreneurship',
    'A program focused on entrepreneurship, opportunity recognition, business planning, enterprise development, and startup management.',
    4,
    'Department of Entrepreneurship',
    'College of Business and Accountancy'
),

(
    UUID(),
    'BSMA',
    'Bachelor of Science in Management Accounting',
    'A four-year non-board program focused on management accounting, financial information, performance management, and organizational decision-making.',
    4,
    'Department of Management Accounting',
    'College of Business and Accountancy'
),


-- =========================================================
-- COLLEGE OF EDUCATION
-- =========================================================

(
    UUID(),
    'BCAED',
    'Bachelor of Culture and Arts Education',
    'A teacher education program specializing in culture and arts education, including visual arts, music, drama, dance, and creative expression.',
    4,
    'Department of Culture and Arts Education',
    'College of Education'
),

(
    UUID(),
    'BECED',
    'Bachelor of Early Childhood Education',
    'A teacher education program focused on the development, education, and teaching of young children.',
    4,
    'Department of Early Childhood Education',
    'College of Education'
),

(
    UUID(),
    'BEED',
    'Bachelor of Elementary Education',
    'A teacher education program preparing students for teaching in elementary education.',
    4,
    'Department of Elementary Education',
    'College of Education'
),

(
    UUID(),
    'BPED',
    'Bachelor of Physical Education',
    'A teacher education program focused on physical education, movement, fitness, sports, and related instruction.',
    4,
    'Department of Physical Education',
    'College of Education'
),

(
    UUID(),
    'BSED',
    'Bachelor of Secondary Education',
    'A teacher education program preparing students for teaching at the secondary education level.',
    4,
    'Department of Secondary Education',
    'College of Education'
),

(
    UUID(),
    'BTLED',
    'Bachelor of Technology and Livelihood Education',
    'A teacher education program focused on technology, livelihood, practical skills, and related teaching competencies.',
    4,
    'Department of Technology and Livelihood Education',
    'College of Education'
),


-- =========================================================
-- COLLEGE OF ENGINEERING
-- =========================================================

(
    UUID(),
    'BSABE',
    'Bachelor of Science in Agricultural and Biosystems Engineering',
    'An engineering program applying engineering science and design to agricultural and biological systems, natural resources, renewable energy, postharvest systems, and environmental management.',
    4,
    'Department of Agricultural and Biosystems Engineering',
    'College of Engineering'
),

(
    UUID(),
    'BSCE',
    'Bachelor of Science in Civil Engineering',
    'An engineering program covering the application of science, mathematics, and engineering principles to civil infrastructure and the built environment.',
    4,
    'Department of Civil Engineering',
    'College of Engineering'
),

(
    UUID(),
    'BSIT',
    'Bachelor of Science in Information Technology',
    'A computing program covering information technology planning, development, integration, operation, management, and maintenance.',
    4,
    'Department of Information Technology',
    'College of Engineering'
),


-- =========================================================
-- COLLEGE OF FISHERIES
-- =========================================================

(
    UUID(),
    'BSF',
    'Bachelor of Science in Fisheries',
    'A program covering fisheries biology, aquaculture, fish production, aquatic ecosystems, capture fisheries, processing, preservation, and fisheries management.',
    4,
    'Department of Fisheries',
    'College of Fisheries'
),


-- =========================================================
-- COLLEGE OF HOME SCIENCE AND INDUSTRY
-- =========================================================

(
    UUID(),
    'BSFT',
    'Bachelor of Science in Food Technology',
    'A program focused on food science, food processing, food manufacturing, quality, safety, and related food industry technologies.',
    4,
    'Department of Food Technology',
    'College of Home Science and Industry'
),

(
    UUID(),
    'BSFTT',
    'Bachelor of Science in Fashion and Textile Technology',
    'A program focused on fashion design, textile technology, apparel development, computer-aided design, merchandising, and entrepreneurship.',
    4,
    'Department of Fashion and Textile Technology',
    'College of Home Science and Industry'
),

(
    UUID(),
    'BSHM',
    'Bachelor of Science in Hospitality Management',
    'A program preparing students for careers in hospitality operations including food and beverage, lodging, leisure, recreation, culinary, and MICE-related services.',
    4,
    'Department of Hospitality Management',
    'College of Home Science and Industry'
),

(
    UUID(),
    'BSTM',
    'Bachelor of Science in Tourism Management',
    'A program focused on tourism operations, travel and tour services, hospitality, MICE management, customer service, and sustainable tourism.',
    4,
    'Department of Tourism Management',
    'College of Home Science and Industry'
),


-- =========================================================
-- COLLEGE OF SCIENCE
-- =========================================================

(
    UUID(),
    'BSBIO',
    'Bachelor of Science in Biology',
    'A science program covering biological, natural, and physical sciences, research, and related biological disciplines.',
    4,
    'Department of Biological Sciences',
    'College of Science'
),

(
    UUID(),
    'BSCHEM',
    'Bachelor of Science in Chemistry',
    'A chemistry program covering laboratory procedures, chemical instrumentation, analytical chemistry, synthetic chemistry, and related applications.',
    4,
    'Department of Chemistry',
    'College of Science'
),

(
    UUID(),
    'BSES',
    'Bachelor of Science in Environmental Science',
    'A multidisciplinary program focused on environmental conservation, management, sustainable agriculture, environmental biology, chemistry, and environmental engineering.',
    4,
    'Department of Environmental Science',
    'College of Science'
),

(
    UUID(),
    'BSMATH',
    'Bachelor of Science in Mathematics',
    'A mathematics program developing analytical and critical thinking skills through theoretical and applied mathematics.',
    4,
    'Department of Mathematics',
    'College of Science'
),

(
    UUID(),
    'BSMET',
    'Bachelor of Science in Meteorology',
    'A program focused on atmospheric science, weather processes, forecasting, climate, and related applications.',
    4,
    'Department of Meteorology',
    'College of Science'
),

(
    UUID(),
    'BSSTAT',
    'Bachelor of Science in Statistics',
    'A program developing expertise in statistical theory, data management, data analysis, visualization, forecasting, research, and data science.',
    4,
    'Department of Statistics',
    'College of Science'
),


-- =========================================================
-- COLLEGE OF VETERINARY SCIENCE AND MEDICINE
-- =========================================================

(
    UUID(),
    'DVM',
    'Doctor of Veterinary Medicine',
    'A professional veterinary medicine program covering animal health, animal production, welfare, public health, clinical care, and One Health.',
    6,
    'Department of Veterinary Medicine',
    'College of Veterinary Science and Medicine'
);