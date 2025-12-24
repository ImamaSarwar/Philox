-- USERS
-- org:1, vol:2, admin:3
INSERT INTO USERS (userId, name, email, password, status, type, registrationDate) VALUES
(1, 'Alice Khan', 'alice.khan@example.com', 'hashedpassword1', 1, 2, '2025-11-01'),
(2, 'Bilal Ahmed', 'bilal.ahmed@example.com', 'hashedpassword2', 1, 2, '2025-11-02'),
(3, 'Catherine Noor', 'catherine.noor@example.com', 'hashedpassword3', 1, 2, '2025-11-03'),
(4, 'Danish Iqbal', 'danish.iqbal@example.com', 'hashedpassword4', 1, 2, '2025-11-04'),
(5, 'Esha Malik', 'esha.malik@example.com', 'hashedpassword5', 1, 2, '2025-11-05'),
(6, 'Farhan Javed', 'farhan.javed@example.com', 'hashedpassword6', 1, 2, '2025-11-06'),
(7, 'Ghazal Rafi', 'ghazal.rafi@example.com', 'hashedpassword7', 1, 2, '2025-11-07'),
(8, 'Hassan Raza', 'hassan.raza@example.com', 'hashedpassword8', 1, 2, '2025-11-08'),
(9, 'Inaya Sohail', 'inaya.sohail@example.com', 'hashedpassword9', 1, 2, '2025-11-09'),
(10, 'Jawad Saeed', 'jawad.saeed@example.com', 'hashedpassword10', 1, 2, '2025-11-10'),
(11, 'Helping Hands Org', 'contact@helpinghands.org', 'hashedpassword11', 1, 1, '2025-11-01'),
(12, 'Youth Empower Org', 'info@youthempower.org', 'hashedpassword12', 1, 1, '2025-11-02'),
(13, 'Green Earth Org', 'support@greenearth.org', 'hashedpassword13', 1, 1, '2025-11-03'),
(14, 'Care & Cure Org', 'hello@carecure.org', 'hashedpassword14', 1, 1, '2025-11-04'),
(15, 'Bright Minds Org', 'team@brightminds.org', 'hashedpassword15', 1, 1, '2025-11-05'),
(16, 'Safe Paws Org', 'desk@safepaws.org', 'hashedpassword16', 1, 1, '2025-11-06'),
(17, 'Unity Builders Org', 'service@unitybuilders.org', 'hashedpassword17', 0, 1, '2025-11-07'),
(18, 'Arts Collective Org', 'mail@artscollective.org', 'hashedpassword18', 1, 1, '2025-11-08'),
(19, 'Relief Responders Org', 'ops@reliefresponders.org', 'hashedpassword19', 1, 1, '2025-11-09'),
(20, 'City Care Org', 'admin@citycare.org', 'hashedpassword20', 1, 1, '2025-11-10'),
(1001, 'System Admin', 'admin@philox.com', 'adminpass', 1, 3, '2025-11-22 09:00:00');

-- ADMIN
INSERT INTO ADMIN (adminId, role)
VALUES (1001, 'SuperAdmin');

-- VOLUNTEER
INSERT INTO VOLUNTEER (volunteerId, phone, cnic, age, city, skills, bio, availability, rating) VALUES
(1, '+923001111111', '11111-1111111-1', 24, 'Karachi', 'teaching,first aid', 'Passionate about education.', 1, 4.2),
(2, '+923002222222', '22222-2222222-2', 29, 'Lahore', 'organizing,medical', 'Experienced medical helper.', 1, 4.8),
(3, '+923003333333', '33333-3333333-3', 31, 'Islamabad', 'environment,cleanup', 'Eco-focused volunteer.', 1, 4.5),
(4, '+923004444444', '44444-4444444-4', 26, 'Rawalpindi', 'mentoring,coaching', 'Youth mentorship advocate.', 1, 4.7),
(5, '+923005555555', '55555-5555555-5', 22, 'Multan', 'logistics,distribution', 'Food drive organizer.', 1, 4.1),
(6, '+923006666666', '66666-6666666-6', 35, 'Hyderabad', 'animal care,rescue', 'Animal welfare supporter.', 1, 4.9),
(7, '+923007777777', '77777-7777777-7', 28, 'Peshawar', 'arts,culture', 'Promotes creative arts.', 1, 4.3),
(8, '+923008888888', '88888-8888888-8', 33, 'Quetta', 'disaster relief,first aid', 'Rapid response trained.', 1, 4.6),
(9, '+923009999999', '99999-9999999-9', 27, 'Sialkot', 'community building,planning', 'Community development planner.', 1, 4.0),
(10,'+923000000000', '00000-0000000-0', 25, 'Faisalabad', 'education,technology', 'STEM teaching assistant.', 1, 4.4);

-- ORGANISATION
INSERT INTO ORGANISATION (organisationId, repName, repCnic, repEmail, repContactNumber, contactNumber, mission, address, website, rating) VALUES
(11, 'Ayesha Ali', '12121-1212121-1', 'rep@helpinghands.org', '+923111111111', '+923111111111', 'Supporting underprivileged families.', '12 Civic Center, Karachi', 'https://helpinghands.org', 4.5),
(12, 'Babar Khan', '13131-1313131-1', 'rep@youthempower.org', '+923122222222', '+923122222222', 'Youth skill development.', '45 Hope Rd, Lahore', 'https://youthempower.org', 4.6),
(13, 'Sadia Qureshi', '14141-1414141-1', 'rep@greenearth.org', '+923133333333', '+923133333333', 'Environmental sustainability.', '7 Green Lane, Islamabad', 'https://greenearth.org', 4.7),
(14, 'Imran Shah', '15151-1515151-1', 'rep@carecure.org', '+923144444444', '+923144444444', 'Community health services.', '88 Wellness Ave, Lahore', 'https://carecure.org',3.2),
(15, 'Nadia Rafi', '16161-1616161-1', 'rep@brightminds.org', '+923155555555', '+923155555555', 'Child education advancement.', '101 Learning St, Karachi', 'https://brightminds.org', 4.8),
(16, 'Rashid Ali', '17171-1717171-1', 'rep@safepaws.org', '+923166666666', '+923166666666', 'Animal rescue and safety.', '22 Paw Park, Hyderabad', 'https://safepaws.org',3.7),
(17, 'Uzma Jamil', '18181-1818181-1', 'rep@unitybuilders.org', '+923177777777', '+923177777777', 'Community infrastructure.', '9 Unity Plaza, Peshawar', 'https://unitybuilders.org', 4.4),
(18, 'Omar Siddiq', '19191-1919191-1', 'rep@artscollective.org', '+923188888888', '+923188888888', 'Arts and cultural promotion.', '3 Art Square, Lahore', 'https://artscollective.org', 4.9),
(19, 'Hiba Sarwar', '20202-2020202-0', 'rep@reliefresponders.org', '+923199999999', '+923199999999', 'Disaster relief operations.', '56 Response Blvd, Multan', 'https://reliefresponders.org', 4.3),
(20, 'Talha Mehmood', '21212-2121212-1', 'rep@citycare.org', '+923200000000', '+923200000000', 'Urban community welfare.', '77 City Center, Islamabad', 'https://citycare.org', 4.0);

-- ORGANISATION_LICENSE
INSERT INTO ORGANISATION_LICENSE (organisationId, registrationAuthority, registrationNumber, issueDate, ntn, registrationProofPath, taxDocumentPath, cnicProofPath) VALUES
(11, 'Govt Authority', 'REG-2025-011', '2025-10-01', 'NTN-111111', '/uploads/organisations/11/reg.pdf', '/uploads/organisations/11/tax.pdf', '/uploads/organisations/11/cnic.pdf'),
(12, 'Govt Authority', 'REG-2025-012', '2025-10-02', 'NTN-121212', '/uploads/organisations/12/reg.pdf', '/uploads/organisations/12/tax.pdf', '/uploads/organisations/12/cnic.pdf'),
(13, 'Govt Authority', 'REG-2025-013', '2025-10-03', 'NTN-131313', '/uploads/organisations/13/reg.pdf', '/uploads/organisations/13/tax.pdf', '/uploads/organisations/13/cnic.pdf'),
(14, 'Govt Authority', 'REG-2025-014', '2025-10-04', 'NTN-141414', '/uploads/organisations/14/reg.pdf', '/uploads/organisations/14/tax.pdf', '/uploads/organisations/14/cnic.pdf'),
(15, 'Govt Authority', 'REG-2025-015', '2025-10-05', 'NTN-151515', '/uploads/organisations/15/reg.pdf', '/uploads/organisations/15/tax.pdf', '/uploads/organisations/15/cnic.pdf'),
(16, 'Govt Authority', 'REG-2025-016', '2025-10-06', 'NTN-161616', '/uploads/organisations/16/reg.pdf', '/uploads/organisations/16/tax.pdf', '/uploads/organisations/16/cnic.pdf'),
(17, 'Govt Authority', 'REG-2025-017', '2025-10-07', 'NTN-171717', '/uploads/organisations/17/reg.pdf', '/uploads/organisations/17/tax.pdf', '/uploads/organisations/17/cnic.pdf'),
(18, 'Govt Authority', 'REG-2025-018', '2025-10-08', 'NTN-181818', '/uploads/organisations/18/reg.pdf', '/uploads/organisations/18/tax.pdf', '/uploads/organisations/18/cnic.pdf'),
(19, 'Govt Authority', 'REG-2025-019', '2025-10-09', 'NTN-191919', '/uploads/organisations/19/reg.pdf', '/uploads/organisations/19/tax.pdf', '/uploads/organisations/19/cnic.pdf'),
(20, 'Govt Authority', 'REG-2025-020', '2025-10-10', 'NTN-202020', '/uploads/organisations/20/reg.pdf', '/uploads/organisations/20/tax.pdf', '/uploads/organisations/20/cnic.pdf');

-- ORGANISATION_SOCIAL_MEDIA
INSERT INTO ORGANISATION_SOCIAL_MEDIA (organisationId, instagramLink, facebookLink, linkedInLink) VALUES
(11, 'https://instagram.com/helpinghands', 'https://facebook.com/helpinghands', 'https://linkedin.com/company/helpinghands'),
(12, 'https://instagram.com/youthempower', 'https://facebook.com/youthempower', 'https://linkedin.com/company/youthempower'),
(13, 'https://instagram.com/greenearth', 'https://facebook.com/greenearth', 'https://linkedin.com/company/greenearth'),
(14, 'https://instagram.com/carecure', 'https://facebook.com/carecure', 'https://linkedin.com/company/carecure'),
(15, 'https://instagram.com/brightminds', 'https://facebook.com/brightminds', 'https://linkedin.com/company/brightminds'),
(16, 'https://instagram.com/safepaws', 'https://facebook.com/safepaws', 'https://linkedin.com/company/safepaws'),
(17, 'https://instagram.com/unitybuilders', 'https://facebook.com/unitybuilders', 'https://linkedin.com/company/unitybuilders'),
(18, 'https://instagram.com/artscollective', 'https://facebook.com/artscollective', 'https://linkedin.com/company/artscollective'),
(19, 'https://instagram.com/reliefresponders', 'https://facebook.com/reliefresponders', 'https://linkedin.com/company/reliefresponders'),
(20, 'https://instagram.com/citycare', 'https://facebook.com/citycare', 'https://linkedin.com/company/citycare');

-- OPPORTUNITY_CATEGORY
INSERT INTO OPPORTUNITY_CATEGORY (categoryId, categoryName) VALUES
(1, 'Education'),
(2, 'Healthcare'),
(3, 'Environmental'),
(4, 'Social Welfare'),
(5, 'Community Development'),
(6, 'Arts & Culture'),
(7, 'Other');

-- OPPORTUNITY
INSERT INTO OPPORTUNITY (opportunityId, organisationId, title, category, description, location, startDate, endDate, closeDate, startTime, duration, capacity, status) VALUES
(1, 11, 'Community Food Drive', '4', 'Distribute food packages to families.', 'Karachi', '2025-12-27', '2025-12-28', '2025-12-25', '09:00', 240, 30, 1),
(2, 12, 'Youth Coding Workshop', '1', 'Teach basic programming to teens.', 'Lahore', '2025-12-28', '2025-12-29', '2025-12-27', '10:00', 180, 25, 1),
(3, 13, 'Tree Plantation Campaign', '3', 'Plant native trees in urban areas.', 'Islamabad', '2025-12-26', '2025-12-28', '2025-12-25', '08:30', 360, 50, 1),
(4, 14, 'Mobile Health Camp', '2', 'Provide free check-ups and consultation.', 'Lahore', '2025-12-15', '2025-12-18', '2025-12-30', '09:30', 240, 20, 1),
(5, 15, 'After-School Tutoring', '1', 'Support primary school students.', 'Karachi', '2025-12-05', '2025-12-20', '2025-12-01', '14:00', 900, 15, 1),
(6, 16, 'Animal Rescue Support', '7', 'Assist with animal shelter tasks.', 'Hyderabad', '2025-12-07', '2025-12-14', '2025-12-03', '11:00', 480, 10, 1),
(7, 17, 'Community Center Renovation', '5', 'Help renovate common facility.', 'Peshawar', '2025-12-09', '2025-12-16', '2025-12-05', '09:00', 600, 18, 1),
(8, 18, 'Art for Inclusion Fair', '6', 'Organize inclusive art activities.', 'Lahore', '2025-12-11', '2025-12-11', '2025-12-07', '13:00', 300, 12, 1),
(9, 19, 'Flood Relief Packing', '7', 'Pack emergency kits for flood zones.', 'Multan', '2025-12-04', '2025-12-06', '2025-11-30', '10:00', 420, 40, 1),
(10, 20, 'Urban Clean-Up Drive', '3', 'Clean littered public spaces.', 'Islamabad', '2025-12-08', '2025-12-08', '2025-12-04', '08:00', 360, 35, 1);

-- OPPORTUNITY (additional open opportunities per organisation)
INSERT INTO OPPORTUNITY (opportunityId, organisationId, title, category, description, location, startDate, endDate, closeDate, startTime, duration, capacity, status) VALUES
(11, 11, 'Family Nutrition Workshop', '1', 'Educate families on balanced diet planning.', 'Karachi', '2025-12-12', '2025-12-12', '2025-12-08', '10:00', 240, 25, 1),
(12, 11, 'Winter Clothing Drive', '4', 'Collect and organise warm clothes for distribution.', 'Karachi', '2025-12-18', '2025-12-19', '2025-12-14', '09:30', 300, 40, 1),
(13, 12, 'Youth Leadership Seminar', '5', 'Interactive sessions to build leadership skills.', 'Lahore', '2025-12-13', '2025-12-14', '2025-12-09', '11:00', 360, 35, 1),
(14, 12, 'STEM Kits Assembly', '1', 'Assemble starter science kits for schools.', 'Lahore', '2025-12-20', '2025-12-21', '2025-12-16', '10:00', 420, 20, 1),
(15, 13, 'River Bank Clean-up', '3', 'Remove waste and plant erosion-control shrubs.', 'Islamabad', '2025-12-09', '2025-12-09', '2025-12-05', '08:00', 300, 45, 1),
(16, 13, 'Recycling Awareness Booth', '3', 'Staff booth to educate about recycling best practices.', 'Islamabad', '2025-12-17', '2025-12-17', '2025-12-13', '09:00', 360, 15, 1),
(17, 14, 'Vaccination Outreach Support', '2', 'Assist teams with crowd flow & registration.', 'Lahore', '2025-12-11', '2025-12-13', '2025-12-07', '09:15', 480, 25, 1),
(18, 14, 'Health Education Materials Prep', '2', 'Prepare printed material for awareness drives.', 'Lahore', '2025-12-22', '2025-12-23', '2025-12-18', '10:30', 360, 18, 1),
(19, 15, 'Reading Circle Facilitation', '1', 'Run guided reading circles for early learners.', 'Karachi', '2025-12-14', '2025-12-19', '2025-12-10', '15:00', 900, 12, 1),
(20, 15, 'Learning Materials Cataloguing', '1', 'Organise donated books & classify them.', 'Karachi', '2025-12-21', '2025-12-24', '2025-12-17', '13:30', 720, 10, 1),
(21, 16, 'Shelter Enrichment Build', '7', 'Construct play structures for rescued animals.', 'Hyderabad', '2025-12-10', '2025-12-12', '2025-12-06', '11:30', 600, 16, 1),
(22, 16, 'Pet Adoption Event Support', '7', 'Help with booth setup & visitor guidance.', 'Hyderabad', '2025-12-19', '2025-12-19', '2025-12-15', '10:00', 300, 14, 1),
(23, 17, 'Tool Inventory & Labelling', '5', 'Catalogue renovation tools and label storage.', 'Peshawar', '2025-12-13', '2025-12-14', '2025-12-09', '09:45', 360, 10, 1),
(24, 17, 'Community Garden Setup', '5', 'Assist in preparing plots & signage.', 'Peshawar', '2025-12-23', '2025-12-24', '2025-12-19', '08:30', 480, 22, 1),
(25, 18, 'Inclusive Art Workshop', '6', 'Facilitate adaptive art activities.', 'Lahore', '2025-12-15', '2025-12-15', '2025-12-11', '12:30', 300, 20, 1),
(26, 18, 'Gallery Event Coordination', '6', 'Support setup & attendee guidance.', 'Lahore', '2025-12-25', '2025-12-25', '2025-12-21', '14:00', 360, 18, 1),
(27, 19, 'Emergency Kit QA', '7', 'Quality check packed relief kits.', 'Multan', '2025-12-12', '2025-12-13', '2025-12-08', '10:15', 420, 28, 1),
(28, 19, 'Logistics Load Scheduling', '7', 'Assist scheduling transport to affected areas.', 'Multan', '2025-12-26', '2025-12-27', '2025-12-22', '09:30', 480, 12, 1),
(29, 20, 'Urban Tree Tagging', '3', 'Tag newly planted trees for tracking.', 'Islamabad', '2025-10-04', '2025-10-06', '2025-10-02', '08:45', 300, 30, 1),
(30, 20, 'Public Awareness Stall', '3', 'Operate info stall on waste reduction.', 'Islamabad', '2025-12-27', '2025-12-27', '2025-12-23', '09:15', 360, 18, 1);

-- APPLICATION
INSERT INTO APPLICATION (applicationId, applicationComment, opportunityId, volunteerId, status) VALUES
(1, 'Ready to help with food distribution.', 1, 5, 0),
(2, 'I can teach Python basics.', 2, 10, 0),
(3, 'Experienced in plantation events.', 3, 3, 0),
(4, 'Medical background for health camp.', 4, 2, 0),
(5, 'Tutoring experience for children.', 5, 1, 0),
(6, 'Love working with rescued animals.', 6, 6, 0),
(7, 'Skilled in renovation logistics.', 7, 9, 0),
(8, 'Arts volunteer for inclusive fair.', 8, 7, 0),
(9, 'Relief operations certified.', 9, 8, 0),
(10,'Can lead clean-up coordination.', 10, 4, 0);

-- APPLICATION (additional pending applications for new opportunities)
INSERT INTO APPLICATION (applicationId, applicationComment, opportunityId, volunteerId, status) VALUES
(11, 'Interested in nutrition outreach.', 11, 1, 0),
(12, 'Great at organising drives.', 12, 5, 0),
(13, 'Leadership mentor experience.', 13, 4, 0),
(14, 'STEM kit assembly background.', 14, 10, 0),
(15, 'Passionate about clean rivers.', 15, 3, 0),
(16, 'Recycling advocacy volunteer.', 16, 7, 0),
(17, 'Medical camp logistics helper.', 17, 2, 0),
(18, 'Health education editing skills.', 18, 9, 0),
(19, 'Enjoy guiding young readers.', 19, 1, 0),
(20, 'Library classification knowledge.', 20, 8, 0),
(21, 'DIY skills for shelter build.', 21, 6, 0),
(22, 'Event coordination experience.', 22, 5, 0),
(23, 'Inventory management exposure.', 23, 9, 0),
(24, 'Gardening & signage skills.', 24, 3, 0),
(25, 'Adaptive art workshop assistant.', 25, 7, 0),
(26, 'Gallery event hosting helper.', 26, 4, 0),
(27, 'Quality assurance careful eye.', 27, 8, 0),
(28, 'Transport logistics mapping.', 28, 2, 0),
(29, 'Experience with tagging systems.', 29, 10, 0),
(30, 'Public speaking & awareness.', 30, 6, 0),
(31, 'Can help with stall setup.', 30, 5, 0),
(32, 'Available for tree tagging.', 29, 4, 0),
(33, 'Interested in relief logistics.', 28, 1, 0),
(34, 'Repeat volunteer for QA tasks.', 27, 6, 0),
(35, 'Art workshop repeat assistant.', 25, 7, 0),
(36, 'Good at this.', 29, 1, 1);


-- RATING (volunteers rating organizations and organizations rating volunteers)
INSERT INTO RATING (ratingId, raterId, rateeId, ratingStars, comment) VALUES
-- Volunteers rating organizations
(1, 1, 11, 5, 'Excellent organization with clear mission and good coordination.'),
(2, 2, 14, 4, 'Well-organized health camp, professional staff.'),
(3, 3, 13, 5, 'Great environmental initiatives, very impactful work.'),
(4, 4, 12, 5, 'Amazing youth programs, really making a difference.'),
(5, 5, 11, 4, 'Good food distribution system, helped many families.'),
(6, 6, 16, 5, 'Passionate about animal welfare, very caring team.'),
(7, 7, 18, 4, 'Creative and inclusive arts programs.'),
(8, 8, 19, 5, 'Professional disaster relief operations, well-trained staff.'),
(9, 9, 17, 4, 'Community development projects are well-planned.'),
(10, 10, 15, 5, 'Excellent educational programs for children.'),
-- Organizations rating volunteers
(11, 11, 1, 4, 'Very dedicated volunteer, great with children.'),
(12, 11, 5, 4, 'Reliable and hardworking, excellent logistics skills.'),
(13, 12, 2, 5, 'Outstanding medical knowledge and very professional.'),
(14, 12, 4, 5, 'Excellent mentor, connects well with youth.'),
(15, 13, 3, 4, 'Knowledgeable about environmental issues, very committed.'),
(16, 14, 2, 5, 'Professional medical support, very helpful.'),
(17, 15, 1, 4, 'Patient and caring with children, great teaching skills.'),
(18, 15, 10, 4, 'Tech-savvy and good at explaining complex concepts.'),
(19, 16, 6, 5, 'Amazing with animals, very compassionate volunteer.'),
(20, 17, 9, 4, 'Great planning skills, very organized volunteer.'),
(21, 18, 7, 4, 'Creative and enthusiastic about arts programs.'),
(22, 19, 8, 5, 'Exceptional disaster response skills, very reliable.'),
(23, 20, 4, 4, 'Good community engagement skills.');


-- BADGE
INSERT INTO BADGE (badgeId, badgeName, badgeCriteria, description, iconPath,participationCount,applicationCount,ratingThreshold) VALUES
(1, 'First Steps', 1, 'Welcome to the community! Your first volunteer experience.', '/assets/badges/starBadge.png', 1, 1, 3.5),
(2, 'Active Contributor', 2, 'Consistently active volunteer with multiple engagements.', '/assets/badges/starBadge.png',5, 5, 4.0),
(3, 'Dedicated Helper', 3, 'Committed volunteer with sustained participation.', '/assets/badges/starBadge.png',10, 10, 4.2),
(4, 'Community Champion', 4, 'Outstanding volunteer making significant community impact.', '/assets/badges/starBadge.png',20, 15, 4.5),
(5, 'Rising Star', 5, 'Promising new volunteer with excellent early performance.', '/assets/badges/starBadge.png',3, 3, 4.0),
(6, 'Excellence Award', 6, 'Exceptional volunteer with outstanding ratings.', '/assets/badges/starBadge.png',1, 1, 4.8),
(7, 'Veteran Volunteer', 7, 'Experienced volunteer with long-term commitment.', '/assets/badges/starBadge.png',15, 12, 4.3);

-- VOLUNTEER_BADGE (assigning badges to volunteers based on their performance)
INSERT INTO VOLUNTEER_BADGE (volunteerId, badgeId, awardedAt) VALUES
(1, 1, '2025-11-05'),  -- First Steps
(1, 2, '2025-11-15'),  -- Active Contributor
(1, 3, '2025-11-20'),  -- Dedicated Helper
(2, 1, '2025-11-03'),  -- First Steps
(2, 2, '2025-11-10'),  -- Active Contributor
(2, 6, '2025-11-18'),  -- Excellence Award (high rating)
(3, 1, '2025-11-04'),  -- First Steps
(3, 2, '2025-11-12'),  -- Active Contributor
(4, 1, '2025-11-06'),  -- First Steps
(4, 5, '2025-11-14'),  -- Rising Star
(4, 2, '2025-11-19'),  -- Active Contributor
(5, 1, '2025-11-07'),  -- First Steps
(5, 2, '2025-11-16'),  -- Active Contributor
(6, 1, '2025-11-08'),  -- First Steps
(6, 6, '2025-11-17'),  -- Excellence Award (highest rating)
(6, 2, '2025-11-21'),  -- Active Contributor
(7, 1, '2025-11-09'),  -- First Steps
(7, 2, '2025-11-18'),  -- Active Contributor
(8, 1, '2025-11-10'),  -- First Steps
(8, 5, '2025-11-19'),  -- Rising Star
(9, 1, '2025-11-11'),  -- First Steps
(9, 5, '2025-11-20'),  -- Rising Star
(10, 1, '2025-11-12'), -- First Steps
(10, 2, '2025-11-21'); -- Active Contributor


